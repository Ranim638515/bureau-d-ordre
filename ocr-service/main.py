from fastapi import FastAPI, UploadFile
import pytesseract
from PIL import Image
import io
import re
import cv2
import numpy as np
import httpx

app = FastAPI()

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"


def extract_num_facture(texte: str) -> str | None:
    # On cible spécifiquement "N° Facture" (avec le symbole °), pas juste "Facture"
    # pour éviter de matcher le titre "FACTURE" en haut du document
    match = re.search(r"N°\s*Facture\s*:?\s*([A-Z0-9\-]+)", texte, re.IGNORECASE)
    if match:
        return match.group(1)

    # Filet de sécurité : si "N° Facture" n'est pas trouvé, on cherche un motif
    # qui ressemble à un vrai numéro de facture (contient au moins un chiffre)
    match = re.search(r"Facture\s*:?\s*([A-Z]{0,3}-?\d[\dA-Z\-]*)", texte, re.IGNORECASE)
    return match.group(1) if match else None


def extract_fournisseur(texte: str) -> str | None:
    # On suppose que le fournisseur est sur la 2e ligne non vide du document
    # (juste après "FACTURE"). C'est une heuristique simple à affiner plus tard.
    lignes = [l.strip() for l in texte.split("\n") if l.strip()]
    for i, ligne in enumerate(lignes):
        if ligne.upper() == "FACTURE" and i + 1 < len(lignes):
            return lignes[i + 1]
    return None


def extract_montant(texte: str, label: str) -> float | None:
    # Cherche "Montant HT : 1200.00 EUR" ou "Montant TTC : 1440.00 EUR"
    pattern = rf"{label}\s*:?\s*([\d\s]+[.,]\d{{2}})\s*(?:EUR|€)?"
    match = re.search(pattern, texte, re.IGNORECASE)
    if match:
        valeur = match.group(1).replace(" ", "").replace(",", ".")
        return float(valeur)
    return None


def extract_date(texte: str) -> str | None:
    match = re.search(r"Date\s*:?\s*(\d{2}/\d{2}/\d{4})", texte, re.IGNORECASE)
    return match.group(1) if match else None


def compute_confidence(data: dict) -> float:
    # Score simple : proportion de champs trouvés (sur 5 champs attendus)
    champs = ["num_facture", "fournisseur", "date_facture", "montant_ht", "montant_ttc"]
    trouves = sum(1 for c in champs if data.get(c) is not None)
    return round(trouves / len(champs), 2)


@app.post("/extract")
async def extract(file: UploadFile):
    contents = await file.read()
    image = Image.open(io.BytesIO(contents))
    image_traitee = preprocess_image(image)
    texte_brut = pytesseract.image_to_string(image_traitee, lang='fra')

    siret = extract_siret(texte_brut)
    siret_valide = valider_siret(siret) if siret else False

    data = {
        "num_facture": extract_num_facture(texte_brut),
        "fournisseur": extract_fournisseur(texte_brut),
        "date_facture": extract_date(texte_brut),
        "montant_ht": extract_montant(texte_brut, "Montant HT"),
        "montant_ttc": extract_montant(texte_brut, "Montant TTC"),
        "type_document": classify_document(texte_brut),
        "siret": siret,
        "siret_valide": siret_valide,
    }
    data["confidence"] = compute_confidence(data)
    data["texte_brut"] = texte_brut

    return data

@app.get("/health")
def health():
    return {"status": "ok"}

def classify_document(texte: str) -> str:
    texte_lower = texte.lower()

    # Score par mots-clés : on compte les occurrences de termes caractéristiques
    scores = {
        "FACTURE": 0,
        "BON_LIVRAISON": 0,
        "CONTRAT": 0,
        "DEVIS": 0,
    }

    if "facture" in texte_lower:
        scores["FACTURE"] += 3
    if "montant ttc" in texte_lower or "montant ht" in texte_lower:
        scores["FACTURE"] += 2
    if "tva" in texte_lower:
        scores["FACTURE"] += 1

    if "bon de livraison" in texte_lower or "bon livraison" in texte_lower:
        scores["BON_LIVRAISON"] += 3
    if "livré" in texte_lower or "livraison" in texte_lower:
        scores["BON_LIVRAISON"] += 1
    if "quantité livrée" in texte_lower:
        scores["BON_LIVRAISON"] += 2

    if "contrat" in texte_lower:
        scores["CONTRAT"] += 3
    if "engagement" in texte_lower or "clause" in texte_lower:
        scores["CONTRAT"] += 1
    if "signataire" in texte_lower or "les parties" in texte_lower:
        scores["CONTRAT"] += 2

    if "devis" in texte_lower:
        scores["DEVIS"] += 3
    if "proposition commerciale" in texte_lower:
        scores["DEVIS"] += 2

    # On retourne le type avec le score le plus élevé
    type_document = max(scores, key=scores.get)

    # Si aucun mot-clé trouvé, on ne peut pas être sûr
    if scores[type_document] == 0:
        return "INDETERMINE"

    return type_document


def preprocess_image(image: Image.Image) -> Image.Image:
    # Convertir l'image PIL en tableau utilisable par OpenCV
    img_array = np.array(image.convert('RGB'))
    img_cv = cv2.cvtColor(img_array, cv2.COLOR_RGB2BGR)

    # 1. Conversion en niveaux de gris (améliore la lecture du texte)
    gray = cv2.cvtColor(img_cv, cv2.COLOR_BGR2GRAY)

    # 2. Débruitage (enlève le "grain" d'une photo/scan de mauvaise qualité)
    denoised = cv2.fastNlMeansDenoising(gray, h=10)

    # 3. Amélioration du contraste (rend le texte plus net face au fond)
    _, thresholded = cv2.threshold(denoised, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

    # Reconvertir en image PIL pour que pytesseract puisse la lire
    return Image.fromarray(thresholded)


def extract_siret(texte: str) -> str | None:
    match = re.search(r"SIRET\s*:?\s*([\d\s]{9,25})", texte, re.IGNORECASE)
    if match:
        # re.sub avec \s supprime TOUS les espaces blancs (espaces, tabulations, retours à la ligne)
        siret = re.sub(r"\s", "", match.group(1))
        if len(siret) >= 14:
            siret = siret[:14]  # on garde exactement les 14 premiers chiffres
            if siret.isdigit():
                return siret
    return None

def valider_siret(siret: str) -> bool:
    if not siret:
        return False
    try:
        response = httpx.get(
            f"https://recherche-entreprises.api.gouv.fr/search?q={siret}",
            timeout=5.0
        )
        data = response.json()
        # Si l'API renvoie au moins un résultat, le SIRET existe
        return data.get("total_results", 0) > 0
    except Exception as e:
        print(f"Erreur validation SIRET : {e}")
        return False