from main import (
    extract_num_facture,
    extract_fournisseur,
    extract_date,
    extract_montant,
    extract_siret,
    classify_document,
    compute_confidence,
)

# Un texte brut d'exemple, simulant ce que Tesseract renverrait pour une vraie facture
TEXTE_FACTURE = """FACTURE

Fournisseur SARL
12 rue de l'Industrie, 75001 Paris
SIRET : 123 456 789 00012

N° Facture : F-2026-0456
Date : 24/07/2026

Désignation Montant HT

Prestation de developpement logiciel 1000.00 EUR

Licence annuelle logiciel 200.00 EUR
Montant HT : 1200.00 EUR
TVA (20%) : 240.00 EUR
Montant TTC : 1440.00 EUR

Merci de votre confiance.
"""


def test_extract_num_facture():
    assert extract_num_facture(TEXTE_FACTURE) == "F-2026-0456"


def test_extract_num_facture_absent():
    assert extract_num_facture("Un texte sans numéro de facture") is None


def test_extract_fournisseur():
    assert extract_fournisseur(TEXTE_FACTURE) == "Fournisseur SARL"


def test_extract_date():
    assert extract_date(TEXTE_FACTURE) == "24/07/2026"


def test_extract_montant_ht():
    assert extract_montant(TEXTE_FACTURE, "Montant HT") == 1200.0


def test_extract_montant_ttc():
    assert extract_montant(TEXTE_FACTURE, "Montant TTC") == 1440.0


def test_extract_montant_absent():
    assert extract_montant(TEXTE_FACTURE, "Montant Inexistant") is None


def test_extract_siret():
    assert extract_siret(TEXTE_FACTURE) == "12345678900012"


def test_extract_siret_absent():
    assert extract_siret("Un texte sans SIRET") is None


def test_classify_document_facture():
    assert classify_document(TEXTE_FACTURE) == "FACTURE"


def test_classify_document_bon_livraison():
    texte = "Bon de livraison\nLivré le 12/07/2026\nQuantité livrée : 10 unités"
    assert classify_document(texte) == "BON_LIVRAISON"


def test_classify_document_indetermine():
    texte = "Un texte quelconque sans mots-clés reconnus"
    assert classify_document(texte) == "INDETERMINE"


def test_compute_confidence_tous_champs_trouves():
    data = {
        "num_facture": "F-2026-0456",
        "fournisseur": "Fournisseur SARL",
        "date_facture": "24/07/2026",
        "montant_ht": 1200.0,
        "montant_ttc": 1440.0,
    }
    assert compute_confidence(data) == 1.0


def test_compute_confidence_aucun_champ_trouve():
    data = {
        "num_facture": None,
        "fournisseur": None,
        "date_facture": None,
        "montant_ht": None,
        "montant_ttc": None,
    }
    assert compute_confidence(data) == 0.0