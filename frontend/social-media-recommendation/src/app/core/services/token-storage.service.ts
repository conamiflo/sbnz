import { Injectable } from '@angular/core';
import {LoginResponse} from '../models/login-response.model';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private readonly ACCESS_TOKEN_KEY = 'accessToken';
  private readonly USERNAME_KEY = 'username';

  setAuthData(loginResponse: LoginResponse): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, loginResponse.accessToken);
    localStorage.setItem(this.USERNAME_KEY, loginResponse.username);
  }

  setAccessToken(token: string): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getUsername(): string | null {
    return localStorage.getItem(this.USERNAME_KEY);
  }

  clear(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.USERNAME_KEY);
  }

  hasToken(): boolean {
    return !!this.getAccessToken();
  }
}
