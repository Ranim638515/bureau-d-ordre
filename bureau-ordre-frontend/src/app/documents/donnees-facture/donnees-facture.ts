import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentService, DonneesFactureModel } from '../../services/document';

@Component({
  selector: 'app-donnees-facture',
  imports: [CommonModule, FormsModule],
  templateUrl: './donnees-facture.html',
  styleUrl: './donnees-facture.css'
})
export class DonneesFactureComponent implements OnInit {
  @Input({ required: true }) documentId!: number;

  donnees = signal<DonneesFactureModel | null>(null);
  modeEdition = signal(false);
  messageSucces = signal('');

  // Copie modifiable des valeurs, utilisée pendant l'édition
  brouillon: Partial<DonneesFactureModel> = {};

  constructor(private documentService: DocumentService) {}

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.documentService.getDonnees(this.documentId).subscribe({
      next: (data) => {
        this.donnees.set(data);
        this.brouillon = { ...data };
      },
      error: (err) => console.error('Erreur chargement données OCR', err)
    });
  }

  // Détermine la couleur d'indicateur selon le score de confiance global
  couleurConfiance(): string {
    const score = this.donnees()?.scoreConfiance ?? 0;
    if (score >= 0.85) return 'vert';
    if (score >= 0.6) return 'orange';
    return 'rouge';
  }

  activerEdition(): void {
    this.modeEdition.set(true);
  }

  annuler(): void {
    this.brouillon = { ...this.donnees() };
    this.modeEdition.set(false);
  }

  enregistrer(): void {
    this.documentService.corriger(this.documentId, this.brouillon).subscribe({
      next: (data) => {
        this.donnees.set(data);
        this.modeEdition.set(false);
        this.messageSucces.set('Corrections enregistrées.');
        setTimeout(() => this.messageSucces.set(''), 3000);
      },
      error: (err) => console.error('Erreur enregistrement', err)
    });
  }
}