import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentList } from './documents/document-list/document-list';
import { Login } from './login/login';
import { DocumentService } from './services/document';

@Component({
  selector: 'app-root',
  imports: [CommonModule, DocumentList, Login],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'bureau-ordre-frontend';

  constructor(public documentService: DocumentService) {}
}