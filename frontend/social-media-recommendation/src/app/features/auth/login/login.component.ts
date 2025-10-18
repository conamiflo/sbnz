import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/login-request.model';
import { LoginResponse } from '../../../core/models/login-response.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', Validators.required]
    });
  }

  get username() { return this.loginForm.get('username'); }
  get password() { return this.loginForm.get('password'); }

  onSubmit(): void {
    this.errorMessage = null; // Reset error message
    this.loginForm.markAllAsTouched();

    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter username and password.';
      return;
    }

    this.isLoading = true;

    const loginData: LoginRequest = this.loginForm.value;

    this.authService.login(loginData).subscribe({
      next: (response: LoginResponse) => {
        this.isLoading = false;
        this.router.navigate(['/posts']);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Login error:', error);
        if (error.status === 401 || error.status === 403) {
          this.errorMessage = 'Invalid username or password.';
        } else {
          this.errorMessage = error.error?.message || 'An error occurred during login.';
        }
      }
    });
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
