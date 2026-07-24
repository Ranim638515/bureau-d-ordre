import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService, DocumentModel } from '../../services/document';

@Component({
  selector: 'app-document-list',
  imports: [CommonModule],
  templateUrl: './document-list.html',
  styleUrl: './document-list.css'
})
export class DocumentList implements OnInit {
  documents = signal<DocumentModel[]>([]);

  constructor(private documentService: DocumentService) {}

  ngOnInit(): void {
    this.documentService.getAll().subscribe({
      next: (data) => {
        this.documents.set(data);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des documents', err);
      }
    });
  }
}