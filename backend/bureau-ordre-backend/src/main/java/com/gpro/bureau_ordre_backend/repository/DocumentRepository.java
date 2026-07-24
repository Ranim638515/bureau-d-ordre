package com.gpro.bureau_ordre_backend.repository;

import com.gpro.bureau_ordre_backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}