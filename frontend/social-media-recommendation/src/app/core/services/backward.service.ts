import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BackwardService {
  private readonly API_URL = `${environment.apiUrl}/backward`;

  constructor(private http: HttpClient) {}

  getRecommendations(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/recommendations/${userId}`);
  }

  getConnectedContent(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/connected/${userId}`);
  }
}
