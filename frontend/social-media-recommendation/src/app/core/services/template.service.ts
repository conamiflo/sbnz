import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {
  private readonly API_URL = `${environment.apiUrl}/recommendations/template-generate-from-excel`;

  constructor(private http: HttpClient) {}

  generateFromExcel(): Observable<string> {
    return this.http.post(this.API_URL, {}, { responseType: 'text' });
  }
}
