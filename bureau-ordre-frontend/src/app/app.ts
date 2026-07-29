import { Component, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentList } from './documents/document-list/document-list';
import { Upload } from './documents/upload/upload';
import { Login } from './login/login';
import { DocumentService } from './services/document';
import { Kanban } from './documents/kanban/kanban';

@Component({
  selector: 'app-root',
  imports: [CommonModule, DocumentList, Upload, Login,Kanban],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'bureau-ordre-frontend';
  documentList = viewChild(DocumentList);

  constructor(public documentService: DocumentService) {}

  onUploaded(): void {
    this.documentList()?.ngOnInit();
  }
}