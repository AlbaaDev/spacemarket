import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth-service';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  protected readonly userService = inject(UserService);
  protected readonly authService = inject(AuthService);
  protected readonly formBuilder = inject(FormBuilder);
  protected readonly router      = inject(Router);
  private   readonly _snackBar   = inject(MatSnackBar);
  protected readonly formHasChanged = signal<boolean>(false);

  profileForm: FormGroup = this.formBuilder.group({
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
      });
    }
    this.profileForm.valueChanges.subscribe(() => {
      this.formHasChanged.set(true);
    });
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
      next: () => {
        this._snackBar.open("Profile updated successfully", "Close", {panelClass: ['snackbar-success'], duration: 10000});
      },
      error: (responseError: any) => {
        this.errorMessage = responseError.error.message;
      }
    });
  }
}
