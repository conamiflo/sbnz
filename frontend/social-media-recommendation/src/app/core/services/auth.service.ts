import { Injectable } from '@angular/core';
import {LoginRequest} from '../models/login-request.model';
import {LoginResponse} from '../models/login-response.model';
import {Observable, tap} from 'rxjs';
import {UserRegistrationRequest} from '../models/user-registration-request.model';
import {TokenStorageService} from './token-storage.service';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {UserResponse} from '../models/user-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/auth`;

  constructor(
    private http: HttpClient,
    private tokenService: TokenStorageService
  ) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, data).pipe(
      tap((response) => {
        this.tokenService.setAuthData(response);
      })
    );
  }

  register(data: UserRegistrationRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.API_URL}/register`, data);
  }

  logout():void {
    this.tokenService.clear();
  }

  isAuthenticated(): boolean {
    const token = this.tokenService.getAccessToken();
    if (!token) {
      return false;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp > currentTime;
    } catch (error) {
      return false;
    }
  }

  getToken(): string | null {
    return this.tokenService.getAccessToken();
  }

  getUsername(): string | null {
    return this.tokenService.getUsername();
  }
}
