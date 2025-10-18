import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {RelevantTrendAlert} from '../models/relevant-trend-alert.model';
import {AudienceSaturationAlert} from '../models/audience-saturation-alert.model';

@Injectable({
  providedIn: 'root'
})
export class CepService {
  private readonly API_URL = `${environment.apiUrl}/cep-tests`;

  constructor(private http: HttpClient) {}

  simulateRelevantTrend(username: string | null): Observable<RelevantTrendAlert[] | string> {
    return this.http.get<RelevantTrendAlert[] | string>(`${this.API_URL}/relevant-trend/${username}`);
  }

  simulateAudienceSaturation(): Observable<AudienceSaturationAlert | string> {
    // Backend враћа или Alert објекат или string "No audience saturation detected."
    return this.http.get<AudienceSaturationAlert | string>(`${this.API_URL}/audience-saturation`);
  }

  simulateViralMomentum(username: string): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/viral-momentum/${username}`);
  }
}
