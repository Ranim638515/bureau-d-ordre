package com.gpro.bureau_ordre_backend.controller;

import com.gpro.bureau_ordre_backend.model.Document;
import com.gpro.bureau_ordre_backend.model.HistoriqueAction;
import com.gpro.bureau_ordre_backend.repository.DocumentRepository;
import com.gpro.bureau_ordre_backend.repository.HistoriqueActionRepository;
import com.gpro.bureau_ordre_backend.workflow.Evenement;
import com.gpro.bureau_ordre_backend.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class WorkflowController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private HistoriqueActionRepository historiqueActionRepository;

    @PostMapping("/{id}/transition")
    public Document appliquerTransition(@PathVariable Long id, @RequestParam Evenement evenement) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document introuvable"));

        boolean accepte = workflowService.appliquerEvenement(document, evenement);

        if (!accepte) {
            throw new RuntimeException(
                "Transition refusée : impossible d'appliquer " + evenement +
                " depuis le statut " + document.getStatut()
            );
        }

        return document;
    }

    @GetMapping("/{id}/historique")
    public List<HistoriqueAction> getHistorique(@PathVariable Long id) {
        return historiqueActionRepository.findByDocumentIdOrderByDateActionDesc(id);
    }
}