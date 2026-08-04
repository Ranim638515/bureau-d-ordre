package com.gpro.bureau_ordre_backend.repository;

import com.gpro.bureau_ordre_backend.model.DonneesFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonneesFactureRepository extends JpaRepository<DonneesFacture, Long> {
    List<DonneesFacture> findByDocumentId(Long documentId);
    List<DonneesFacture> findByFournisseurAndMontantTtc(String fournisseur, java.math.BigDecimal montantTtc);
}
