package com.gpro.bureau_ordre_backend.controller;

import com.gpro.bureau_ordre_backend.model.Document;
import com.gpro.bureau_ordre_backend.model.DonneesFacture;
import com.gpro.bureau_ordre_backend.repository.DocumentRepository;
import com.gpro.bureau_ordre_backend.repository.DonneesFactureRepository;
import com.gpro.bureau_ordre_backend.service.OcrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DonneesFactureRepository donneesFactureRepository;

    @Autowired
    private OcrClient ocrClient;

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public Document upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 1. Sauvegarde du fichier
        new File(UPLOAD_DIR).mkdirs();
        String path = UPLOAD_DIR + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.write(Paths.get(path), file.getBytes());

        Document doc = new Document();
        doc.setNomFichier(file.getOriginalFilename());
        doc.setCheminFichier(path);
        doc.setStatut("RECU");
        doc = documentRepository.save(doc);

        // 2. Appel au microservice Python pour l'OCR
        try {
            Map<String, Object> resultatOcr = ocrClient.extract(path);

            DonneesFacture donnees = new DonneesFacture();
            donnees.setDocument(doc);
            donnees.setNumFacture((String) resultatOcr.get("num_facture"));
            donnees.setFournisseur((String) resultatOcr.get("fournisseur"));
            donnees.setTypeDocument((String) resultatOcr.get("type_document"));
            donnees.setSiret((String) resultatOcr.get("siret"));
            if (resultatOcr.get("siret_valide") != null) {
                donnees.setSiretValide((Boolean) resultatOcr.get("siret_valide"));
            }

            if (resultatOcr.get("montant_ht") != null) {
                donnees.setMontantHt(BigDecimal.valueOf(((Number) resultatOcr.get("montant_ht")).doubleValue()));
            }
            if (resultatOcr.get("montant_ttc") != null) {
                donnees.setMontantTtc(BigDecimal.valueOf(((Number) resultatOcr.get("montant_ttc")).doubleValue()));
            }
            if (resultatOcr.get("confidence") != null) {
                donnees.setScoreConfiance(BigDecimal.valueOf(((Number) resultatOcr.get("confidence")).doubleValue()));
            }

            // Détection de doublon : même fournisseur + même montant TTC
            boolean doublonDetecte = false;
            if (donnees.getFournisseur() != null && donnees.getMontantTtc() != null) {
                List<DonneesFacture> existants = donneesFactureRepository
                    .findByFournisseurAndMontantTtc(donnees.getFournisseur(), donnees.getMontantTtc());
                doublonDetecte = !existants.isEmpty();
            }
            donnees.setDoublonDetecte(doublonDetecte);

            donneesFactureRepository.save(donnees);

            doc.setStatut("OCR_TERMINE");
            doc = documentRepository.save(doc);

        } catch (Exception e) {
            doc.setStatut("OCR_ECHEC");
            doc = documentRepository.save(doc);
            System.err.println("Erreur OCR : " + e.getMessage());
        }

        return doc;
    }

    @GetMapping
    public List<Document> getAll() {
        return documentRepository.findAll();
    }
}