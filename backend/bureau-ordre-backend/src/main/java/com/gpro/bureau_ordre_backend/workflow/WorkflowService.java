package com.gpro.bureau_ordre_backend.workflow;

import com.gpro.bureau_ordre_backend.model.Document;
import com.gpro.bureau_ordre_backend.model.HistoriqueAction;
import com.gpro.bureau_ordre_backend.repository.DocumentRepository;
import com.gpro.bureau_ordre_backend.repository.HistoriqueActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    @Autowired
    private StateMachineFactory<Statut, Evenement> stateMachineFactory;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private HistoriqueActionRepository historiqueActionRepository;

    public boolean appliquerEvenement(Document document, Evenement evenement) {
        StateMachine<Statut, Evenement> machine = stateMachineFactory.getStateMachine();

        Statut statutActuel = Statut.valueOf(document.getStatut());

        machine.getStateMachineAccessor()
            .doWithAllRegions(access ->
                access.resetStateMachine(
                    new org.springframework.statemachine.support.DefaultStateMachineContext<Statut, Evenement>(
                        statutActuel, null, null, null
                    )
                )
            );
        machine.start();

        boolean accepte = machine.sendEvent(MessageBuilder.withPayload(evenement).build());

        if (accepte) {
            Statut nouveauStatut = machine.getState().getId();

            // Enregistrement de l'historique AVANT de changer le statut du document,
            // pour garder une trace de "d'où on vient" et "où on va"
            HistoriqueAction action = new HistoriqueAction();
            action.setDocument(document);
            action.setStatutPrecedent(statutActuel.name());
            action.setStatutSuivant(nouveauStatut.name());
            action.setEvenement(evenement.name());
            historiqueActionRepository.save(action);

            document.setStatut(nouveauStatut.name());
            documentRepository.save(document);
        }

        machine.stop();
        return accepte;
    }
}