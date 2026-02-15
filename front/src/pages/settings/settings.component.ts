import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { switchMap } from 'rxjs';
import { AuthService } from '../../services/auth/auth-service';
import { UserService } from '../../services/user/user.service';
import { MustMatch } from '../../validators/MustMatch';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent {
  protected readonly userService = inject(UserService);
  protected readonly authService = inject(AuthService);
  protected readonly formBuilder = inject(FormBuilder);
  protected readonly router      = inject(Router);

  protected readonly usernameFromHasChanged = signal<boolean>(false);
  protected readonly passwordFormHasChanged = signal<boolean>(false);
  errorMessage: string | null = null;
  protected readonly userNameForm: FormGroup = this.formBuilder.group({
    email: ['', [Validators.email, Validators.required]]
  });

  protected readonly passwordForm: FormGroup = this.formBuilder.group({
    currentPassword: ['', [Validators.required, Validators.minLength(8)]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required, Validators.minLength(8)]],
  }, { validators: MustMatch('newPassword', 'confirmPassword') });

  get email() {
    return this.userNameForm.get('email');
  }

  get currentPassword() {
    return this.passwordForm.get('currentPassword');
  }

  get newPassword() {
    return this.passwordForm.get('newPassword');
  }

  get confirmPassword() {
    return this.passwordForm.get('confirmPassword');
  }

  constructor() { }

  ngOnInit(): void {
    const currentUser = this.authService.currentUser();
    if (currentUser) {
      this.userNameForm.patchValue({
        email: currentUser.email,
      });
    }
    this.userNameForm.valueChanges.subscribe(() => {
      this.usernameFromHasChanged.set(true);
    });
    this.passwordForm.valueChanges.subscribe(() => {
      this.passwordFormHasChanged.set(true);
    });
  }

  onSubmitUserName() {
    this.errorMessage = null;
    this.userService.updateSettings(this.userNameForm.value).pipe(
      switchMap(() => this.authService.logout()),
      switchMap(() => this.router.navigate(['/app-login']))
    ).subscribe({
      error: (responseError: any) => {
        this.errorMessage = responseError.error.message;
      }
    });
  }

  onSubmitPassword() {
    this.errorMessage = null;
    this.userService.updatePassword(this.passwordForm.value).pipe(
      switchMap(() => this.authService.logout()),
      switchMap(() => this.router.navigate(['/app-login']))
    ).subscribe({
      error: (responseError: any) => {
        this.errorMessage = responseError.error.message;
      }
    });
  }
}

