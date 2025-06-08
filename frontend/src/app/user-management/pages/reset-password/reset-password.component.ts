import { Component } from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {
    TuiButton,
    TuiError,
    TuiIcon,
    TuiLabel,
    TuiTextfieldComponent,
    TuiTextfieldDirective,
    TuiTitle
} from "@taiga-ui/core";
import {TuiFieldErrorPipe, TuiPassword, TuiTooltip} from "@taiga-ui/kit";
import {TuiForm, TuiHeader} from "@taiga-ui/layout";
import {TuiInputCopyModule, TuiInputDateModule} from "@taiga-ui/legacy";
import {TuiDay} from '@taiga-ui/cdk';
import { Router } from '@angular/router';
import {supabase} from '../../supabase.service';
import {UserDataDto} from '../../../user_db.dto/user-data.dto';
import {UUID} from 'node:crypto';

@Component({
  selector: 'app-reset-password',
    imports: [
        AsyncPipe,
        ReactiveFormsModule,
        TuiButton,
        TuiError,
        TuiFieldErrorPipe,
        TuiForm,
        TuiHeader,
        TuiIcon,
        TuiInputCopyModule,
        TuiInputDateModule,
        TuiLabel,
        TuiPassword,
        TuiTextfieldComponent,
        TuiTextfieldDirective,
        TuiTitle,
        TuiTooltip
    ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {

  protected changePassForm = new FormGroup({
    passwordValue: new FormControl('', Validators.required)
  });

  constructor(private router: Router) {
  }

  async onSubmit(): Promise<void> {
    if (this.changePassForm.invalid) {
      this.changePassForm.markAllAsTouched();
      return;
    }
    const { passwordValue } = this.changePassForm.value;
    console.log('About to call Supabase:', passwordValue);

    const { data, error } = await supabase.auth.updateUser({
      password: passwordValue!
    });

    console.log('Supabase result:', { data, error });

    if (error) {
      alert(error.message);
      return;
    }
    alert('Password change successful!');

    this.router.navigate(['/profile']);
  }

}
