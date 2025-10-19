import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from '../models/user-response.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly API_URL = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}


  getCurrentUserProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API_URL}/me`);
  }

  getUserProfileById(userId: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API_URL}/${userId}`);
  }

  getUserProfileByUsername(username: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API_URL}/${username}`);
  }
}
