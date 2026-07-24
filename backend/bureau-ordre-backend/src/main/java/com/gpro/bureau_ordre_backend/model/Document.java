package com.gpro.bureau_ordre_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomFichier;
    private String cheminFichier;
    private String statut = "RECU";
    private LocalDateTime dateUpload = LocalDateTime.now();
}