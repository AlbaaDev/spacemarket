import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../services/user/user.service';
import { AuthService } from '../../services/auth/auth-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

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
  protected readonly router = inject(Router);
  protected readonly formHasChanged = signal<boolean>(false);

  errorMessage : string | null = null;

  settingsForm: FormGroup = this.formBuilder.group({
    email: ['', [Validators.email, Validators.required]],
    password: ['', Validators.required, Validators.minLength(8)],
  });

  get email() {
    return this.settingsForm.get('email');
  }

  get password() {
    return this.settingsForm.get('password');
  }

  constructor() {

  }

  ngOnInit(): void {
    const currentUser = this.authService.currentUser();
    if (currentUser) {
      this.settingsForm.patchValue({
        email: currentUser.email,
      });
    }
    this.settingsForm.valueChanges.subscribe(() => {
      this.formHasChanged.set(true);
    });
  }

  onSubmit() {
    this.errorMessage = null;
    this.userService.updateSettings(this.settingsForm.value).subscribe({
            next: () => {
                this.authService.logout();
                this.router.navigate(['/app-login']);
            },
            error: (reponseError: any) => {
                this.errorMessage = reponseError.error.message;
            }
    });
  }
}
