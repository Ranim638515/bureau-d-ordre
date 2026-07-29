package com.gpro.bureau_ordre_backend.repository;

import com.gpro.bureau_ordre_backend.model.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
    List<HistoriqueAction> findByDocumentIdOrderByDateActionDesc(Long documentId);
}