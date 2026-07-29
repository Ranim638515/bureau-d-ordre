package com.gpro.bureau_ordre_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "donnees_facture")
@Data
public class DonneesFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    private String numFacture;
    private String fournisseur;
    private BigDecimal montantHt;
    private BigDecimal montantTtc;
    private BigDecimal scoreConfiance;
    private String typeDocument;
}