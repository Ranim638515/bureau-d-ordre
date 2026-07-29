import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService, DocumentModel } from '../../services/document';

@Component({
  selector: 'app-kanban',
  imports: [CommonModule],
  templateUrl: './kanban.html',
  styleUrl: './kanban.css'
})
export class Kanban implements OnInit {
  documents = signal<DocumentModel[]>([]);

  // Liste ordonnée des colonnes à afficher (dans l'ordre du workflow)
  colonnes = [
    'RECU',
    'OCR_TERMINE',
    'OCR_ECHEC',
    'A_VALIDER_COMPTABLE',
    'A_VALIDER_DAF',
    'VALIDE',
    'REJETE',
    'INSERE_ERP'
  ];

  // Regroupe automatiquement les documents par statut à chaque changement de `documents`
  documentsParStatut = computed(() => {
    const groupes: Record<string, DocumentModel[]> = {};
    for (const colonne of this.colonnes) {
      groupes[colonne] = this.documents().filter(doc => doc.statut === colonne);
    }
    return groupes;
  });

  constructor(private documentService: DocumentService) {}

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.documentService.getAll().subscribe({
      next: (data) => this.documents.set(data),
      error: (err) => console.error('Erreur lors du chargement des documents', err)
    });
  }
}