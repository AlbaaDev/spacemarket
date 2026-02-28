import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatInputModule } from "@angular/material/input";
import { CompanyService } from "../../../../services/company/company.service";

@Component({
    selector: 'delete-company-modal',
    templateUrl: './delete-company-modal.html',
    styleUrl: './delete-company-modal.css',
    imports: [MatDialogModule, MatButtonModule, MatInputModule, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteCompanyModal {
    private readonly companyService = inject(CompanyService);
    private readonly formBuilder = inject(FormBuilder);

    constructor() { }

    confirmDeleteCompany() {
        this.companyService.canDeleteCompanies.set(true);
    }
}