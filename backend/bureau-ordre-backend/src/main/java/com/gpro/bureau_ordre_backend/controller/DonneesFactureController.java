package com.gpro.bureau_ordre_backend.controller;

import com.gpro.bureau_ordre_backend.model.DonneesFacture;
import com.gpro.bureau_ordre_backend.repository.DonneesFactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DonneesFactureController {

    @Autowired
    private DonneesFactureRepository donneesFactureRepository;

    @GetMapping("/{id}/donnees")
    public DonneesFacture getDonnees(@PathVariable Long id) {
        List<DonneesFacture> resultats = donneesFactureRepository.findByDocumentId(id);
        if (resultats.isEmpty()) {
            throw new RuntimeException("Aucune donnée OCR trouvée pour ce document");
        }
        return resultats.get(0);
    }

    @PatchMapping("/{id}/corriger")
    public DonneesFacture corriger(@PathVariable Long id, @RequestBody Map<String, Object> corrections) {
        List<DonneesFacture> resultats = donneesFactureRepository.findByDocumentId(id);
        if (resultats.isEmpty()) {
            throw new RuntimeException("Aucune donnée OCR trouvée pour ce document");
        }
        DonneesFacture donnees = resultats.get(0);

        if (corrections.containsKey("numFacture")) {
            donnees.setNumFacture((String) corrections.get("numFacture"));
        }
        if (corrections.containsKey("fournisseur")) {
            donnees.setFournisseur((String) corrections.get("fournisseur"));
        }
        if (corrections.containsKey("montantHt")) {
            donnees.setMontantHt(BigDecimal.valueOf(((Number) corrections.get("montantHt")).doubleValue()));
        }
        if (corrections.containsKey("montantTtc")) {
            donnees.setMontantTtc(BigDecimal.valueOf(((Number) corrections.get("montantTtc")).doubleValue()));
        }

        return donneesFactureRepository.save(donnees);
    }
}