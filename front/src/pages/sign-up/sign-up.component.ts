import { Component, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { AuthService } from '../../services/auth/auth-service';

@Component({
  selector: 'app-sign-up',
  imports: [ReactiveFormsModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})
export class SignUpComponent {
    private formBuilder = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);
    signUpForm : FormGroup;

    errorMessage: string | null = null;

    constructor() {
      this.signUpForm = this.formBuilder.group({
          email: ['', [Validators.required, Validators.email]],
          password: ['', [Validators.required, Validators.minLength(8)]],
          firstName: ['', [Validators.required]],
          lastName: ['', [Validators.required]]
      });
    }

    get email() {
      return this.signUpForm.get('email') as FormControl | null;
    }
 
    get password() {
      return this.signUpForm.get('password') as FormControl | null;
    }

    get firstName() {
      return this.signUpForm.get('firstName') as FormControl | null;
    }

    get lastName() {
      return this.signUpForm.get('lastName') as FormControl | null;
    }

    onSubmit() {
      this.authService.signUp(this.signUpForm).subscribe({
        next: () => {
          this.router.navigate(['/app-login']);      
        },
        error: (reponseError) => {
          this.errorMessage = reponseError.error.message;
        }  
      });
    }
}
