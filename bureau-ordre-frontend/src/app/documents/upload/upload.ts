import { Component, signal, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService } from '../../services/document';

@Component({
  selector: 'app-upload',
  imports: [CommonModule],
  templateUrl: './upload.html',
  styleUrl: './upload.css'
})
export class Upload {
  selectedFile = signal<File | null>(null);
  isDragging = signal(false);
  uploading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  // Permet de prévenir le composant parent (app.ts) qu'un upload a réussi,
  // pour rafraîchir la liste des documents
  uploaded = output<void>();

  constructor(private documentService: DocumentService) {}

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
    const file = event.dataTransfer?.files?.[0];
    if (file) {
      this.selectedFile.set(file);
      this.errorMessage.set('');
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.selectedFile.set(file);
      this.errorMessage.set('');
    }
  }

  onSubmit(): void {
    const file = this.selectedFile();
    if (!file) return;

    this.uploading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.documentService.upload(file).subscribe({
      next: () => {
        this.uploading.set(false);
        this.successMessage.set(`"${file.name}" envoyé avec succès.`);
        this.selectedFile.set(null);
        this.uploaded.emit(); // signale au parent de rafraîchir la liste
      },
      error: (err) => {
        this.uploading.set(false);
        this.errorMessage.set("Erreur lors de l'envoi du fichier.");
        console.error('Erreur upload', err);
      }
    });
  }
}