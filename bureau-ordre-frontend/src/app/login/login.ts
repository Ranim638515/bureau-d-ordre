import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentService } from '../services/document';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private documentService: DocumentService) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.documentService.login(this.username, this.password).subscribe({
      next: () => {
      },
      error: (err) => {
        this.errorMessage = 'Identifiants incorrects';
        console.error('Erreur de connexion', err);
      }
    });
  }
}