package com.gpro.bureau_ordre_backend.workflow;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
public class WorkflowConfig extends EnumStateMachineConfigurerAdapter<Statut, Evenement> {

    @Override
    public void configure(StateMachineStateConfigurer<Statut, Evenement> states) throws Exception {
        states
            .withStates()
            .initial(Statut.RECU)
            .states(java.util.EnumSet.allOf(Statut.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<Statut, Evenement> transitions) throws Exception {
        transitions
            .withExternal()
                .source(Statut.RECU).target(Statut.OCR_TERMINE)
                .event(Evenement.OCR_VALIDE)
                .and()
            .withExternal()
                .source(Statut.OCR_TERMINE).target(Statut.A_VALIDER_COMPTABLE)
                .event(Evenement.ENVOYER_VALIDATION)
                .and()
            .withExternal()
                .source(Statut.A_VALIDER_COMPTABLE).target(Statut.A_VALIDER_DAF)
                .event(Evenement.APPROUVER)
                .and()
            .withExternal()
                .source(Statut.A_VALIDER_DAF).target(Statut.VALIDE)
                .event(Evenement.APPROUVER)
                .and()
            .withExternal()
                .source(Statut.A_VALIDER_COMPTABLE).target(Statut.REJETE)
                .event(Evenement.REJETER)
                .and()
            .withExternal()
                .source(Statut.A_VALIDER_DAF).target(Statut.REJETE)
                .event(Evenement.REJETER)
                .and()
            .withExternal()
                .source(Statut.VALIDE).target(Statut.INSERE_ERP)
                .event(Evenement.INSERER_ERP);
    }
}