package com.gpro.bureau_ordre_backend.workflow;

public enum Evenement {
    OCR_VALIDE,      // l'OCR est terminé avec un score de confiance acceptable
    ENVOYER_VALIDATION, // envoyer le document en circuit de validation
    APPROUVER,       // un valideur approuve
    REJETER,         // un valideur rejette
    INSERER_ERP      // insertion finale dans l'ERP
}