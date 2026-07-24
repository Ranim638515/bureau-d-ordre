from fastapi import FastAPI, UploadFile

app = FastAPI()

@app.post("/extract")
async def extract(file: UploadFile):
    return {
        "num_facture": "F-2026-001",
        "fournisseur": "Fournisseur Mock SARL",
        "montant_ht": 120.0,
        "montant_ttc": 144.0,
        "confidence": 0.87
    }

@app.get("/health")
def health():
    return {"status": "ok"}