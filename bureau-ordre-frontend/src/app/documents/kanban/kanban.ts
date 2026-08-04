import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService, DocumentModel } from '../../services/document';
import { DonneesFactureComponent } from '../donnees-facture/donnees-facture';

@Component({
  selector: 'app-kanban',
  imports: [CommonModule, DonneesFactureComponent],
  templateUrl: './kanban.html',
  styleUrl: './kanban.css'
})
export class Kanban implements OnInit {
  documents = signal<DocumentModel[]>([]);
  documentOuvertId = signal<number | null>(null);

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

  toggleCarte(id: number): void {
    this.documentOuvertId.set(this.documentOuvertId() === id ? null : id);
  }
}