import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {environment} from '../../environments/environment';
import {RecommendationResponse} from '../models/recommendation-response.model';

@Injectable({ providedIn: 'root' })
export class RecommendationService {
  private readonly API_URL = `${environment.apiUrl}/recommendations`;

  constructor(private http: HttpClient) {}

  getRecommendations(): Observable<RecommendationResponse> {
    return this.http.get<RecommendationResponse>(`${this.API_URL}/generate`);
  }
}
