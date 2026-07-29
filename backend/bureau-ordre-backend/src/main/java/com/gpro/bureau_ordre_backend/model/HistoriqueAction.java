package com.gpro.bureau_ordre_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_action")
@Data
public class HistoriqueAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    private String statutPrecedent;
    private String statutSuivant;
    private String evenement;
    private LocalDateTime dateAction = LocalDateTime.now();
}