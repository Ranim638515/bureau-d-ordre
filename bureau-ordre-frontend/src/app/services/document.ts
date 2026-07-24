import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface DocumentModel {
  id: number;
  nomFichier: string;
  cheminFichier: string;
  statut: string;
  dateUpload: string;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private apiUrl = 'http://localhost:8080/api/documents';
  private authUrl = 'http://localhost:8080/auth';
  private token: string | null = null;

  loggedIn = signal(false);

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.authUrl}/login`, { username, password }).pipe(
      tap(response => {
        this.token = response.token;
        this.loggedIn.set(true);
      })
    );
  }

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.token}`
    });
  }

  getAll(): Observable<DocumentModel[]> {
    return this.http.get<DocumentModel[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  upload(file: File): Observable<DocumentModel> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<DocumentModel>(`${this.apiUrl}/upload`, formData, { headers: this.getAuthHeaders() });
  }
}