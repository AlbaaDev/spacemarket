import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth-service';
import { UserService } from '../../services/user/user.service';
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  protected readonly userService = inject(UserService);
  protected readonly authService = inject(AuthService);
  protected readonly formBuilder = inject(FormBuilder);
  protected readonly router = inject(Router);


  profileForm: FormGroup = this.formBuilder.group({
    email: ['', [Validators.email, Validators.required]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required]
  });

  errorMessage: string | null = null;

  constructor() {}

  ngOnInit(): void {
    const currentUser = this.authService.currentUser();
    if (currentUser) {
      this.profileForm.patchValue({
        firstName: currentUser.firstName,
        lastName: currentUser.lastName,
        email: currentUser.email,
      });
    }
  }

  get email() {
    return this.profileForm.get('email');
  }

  get firstName() {
    return this.profileForm.get('firstName');
  }

  get lastName() {
    return this.profileForm.get('lastName');
  }

  onSubmit() {
    this.errorMessage = null;
    this.userService.updateProfile(this.profileForm.value).subscribe({
            next: (response: any) => {
                console.log(typeof response, response);
            },
            error: (reponseError: any) => {
                this.errorMessage = reponseError.error.message;
            }
    });
  }
}
