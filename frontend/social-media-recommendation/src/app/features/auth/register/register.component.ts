import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {UserRegistrationRequest} from '../../../core/models/user-registration-request.model';
import {AuthService} from '../../../core/services/auth.service';



@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      age: [null as number | null, [Validators.min(0), Validators.max(120)]],
      location: [''],
      gender: [null as string | null],
      interests: [''],
      creatorType: [null as string | null],
      audienceSize: [null as number | null, [Validators.min(0)]]
    }, { validators: this.passwordMatchValidator });
  }

  // --- Getters for easier access in template ---
  get name() { return this.registerForm.get('name'); }
  // get surname() { return this.registerForm.get('surname'); } // Removed
  get username() { return this.registerForm.get('username'); }
  get password() { return this.registerForm.get('password'); }
  get confirmPassword() { return this.registerForm.get('confirmPassword'); }
  get age() { return this.registerForm.get('age'); }
  get location() { return this.registerForm.get('location'); }
  get gender() { return this.registerForm.get('gender'); }
  get interests() { return this.registerForm.get('interests'); }
  get creatorType() { return this.registerForm.get('creatorType'); }
  get audienceSize() { return this.registerForm.get('audienceSize'); }
  // --- End Getters ---

  onSubmit(): void {
    this.errorMessage = null;
    this.successMessage = null;
    this.registerForm.markAllAsTouched();

    if (this.registerForm.invalid) {
      this.errorMessage = 'Please correct the errors in the form.';
      return;
    }

    this.isLoading = true;

    // Prepare DTO from form values
    const formValue = this.registerForm.value;
    const dtoToSend: UserRegistrationRequest = {
      username: formValue.username,
      password: formValue.password,
      name: formValue.name,
      age: formValue.age ?? null,
      location: formValue.location || null,
      gender: formValue.gender || null,
      interests: formValue.interests
          ?.split(',')
          ?.map((interest: string) => interest.trim())
          ?.filter((interest: string) => interest !== '')
        ?? [],
      creatorType: formValue.creatorType || null,
      audienceSize: formValue.audienceSize ?? null
    };

    this.authService.register(dtoToSend).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.successMessage = 'Registration successful! You can now log in.';
        console.log('Registration response:', response);
        this.registerForm.reset();
      },
      error: (error: { status: number; error: { message: string; }; }) => {
        this.isLoading = false;
        console.error('Registration error:', error);
        if (error.status === 400 && error.error?.message?.toLowerCase().includes('username already exists')) {
          this.errorMessage = 'Username already exists. Please choose another one.';
          this.username?.setErrors({ usernameTaken: true }); // Optionally mark the field
        } else {
          this.errorMessage = error.error?.message || 'An unexpected error occurred during registration.';
        }
      }
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    } else if (confirmPassword) {
      const errors = confirmPassword.errors;
      if (errors && errors['passwordMismatch']) {
        delete errors['passwordMismatch'];
        if (Object.keys(errors).length === 0) {
          confirmPassword.setErrors(null);
        } else {
          confirmPassword.setErrors(errors);
        }
      }
    }
    return null;
  }
}
