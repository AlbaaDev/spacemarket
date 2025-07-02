import { Component, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthServiceService } from '../../app/services/auth-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sign-up',
  imports: [ReactiveFormsModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})
export class SignUpComponent {
    private formBuilder = inject(FormBuilder);
    private authService = inject(AuthServiceService);
    private router = inject(Router);
    signUpForm : FormGroup;

    constructor() {
      this.signUpForm = this.formBuilder.group({
          email: ['', [Validators.required, Validators.email]],
          password: ['', [Validators.required, Validators.minLength(8)]],
          firstName: ['', [Validators.required]],
          lastName: ['', [Validators.required]]
      });
    }

    get email() {
      return this.signUpForm.get('email') as FormControl;
    }

    get password() {
      return this.signUpForm.get('password') as FormControl;
    }

    get firstName() {
      return this.signUpForm.get('firstName') as FormControl;
    }

    get lastName() {
      return this.signUpForm.get('lastName') as FormControl;
    }

    onSubmit() {
      this.authService.signUp(this.signUpForm).subscribe((res) =>  {
        if(res) {
          this.router.navigate(['/app-login']);
        }
      });      
    }
}
