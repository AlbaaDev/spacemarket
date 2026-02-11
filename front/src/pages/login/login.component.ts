import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth-service';
import { environment } from '../../environments/environment';

@Component({
    selector: 'app-login',
    imports: [RouterLink, ReactiveFormsModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {
    private readonly formBuilder = inject(FormBuilder);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);

    errorMessage: string | null = null;
    loginForm: FormGroup;

    constructor() {
        this.loginForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(8)]],
        });
    }

    get email() {
        return this.loginForm.get('email');
    }

    get password() {
        return this.loginForm.get('password');
    }

    onSubmit() {
        this.errorMessage = null;
        this.authService.login(this.loginForm).subscribe({
            next: () => {
                this.router.navigate(['/app-dashboard']);
            },
            error: (responseError) => {
                 // Gestion robuste des différents types d'erreurs
            if (responseError.status === 0) {
                // Erreur réseau (serveur inaccessible, CORS, etc.)
                this.errorMessage = 'Impossible de contacter le serveur. Vérifiez que le backend est démarré sur ' + environment.baseUrl;
            } else if (responseError.status === 401 || responseError.status === 403) {
                // Identifiants incorrects
                this.errorMessage = responseError.error?.message || 'Identifiants incorrects';
            } else if (responseError.error && typeof responseError.error === 'object' && responseError.error.message) {
                // Message d'erreur du serveur
                this.errorMessage = responseError.error.message;
            } else if (responseError.error && typeof responseError.error === 'string') {
                // Message d'erreur simple en string
                this.errorMessage = responseError.error;
            } else if (responseError.message) {
                // Message d'erreur HTTP générique
                this.errorMessage = responseError.message;
            } else {
                // Erreur inconnue
                this.errorMessage = 'Une erreur inattendue est survenue lors de la connexion';
            }
            }
        });
    }
}
