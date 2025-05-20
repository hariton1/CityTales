import { Component } from '@angular/core';
import {TuiButton, TuiError, TuiLabel, TuiTextfieldComponent, TuiTextfieldDirective, TuiTitle, TuiIcon} from '@taiga-ui/core';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TuiFieldErrorPipe, TuiPassword, TuiTooltip} from '@taiga-ui/kit';
import {AsyncPipe} from '@angular/common';
import {TuiInputModule} from '@taiga-ui/legacy';

@Component({
  selector: 'app-login',
  imports: [
    TuiButton,
    TuiForm,
    TuiTextfieldComponent,
    TuiHeader,
    TuiTitle,
    TuiLabel,
    ReactiveFormsModule,
    TuiTextfieldDirective,
    TuiError,
    TuiFieldErrorPipe,
    AsyncPipe,
    TuiPassword,
    TuiTooltip,
    TuiIcon,
    TuiInputModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  protected loginForm = new FormGroup({
    email: new FormControl('', Validators.required),
    passwordValue: new FormControl('', Validators.required),
  });

  onSubmit(): void {
    if (this.loginForm.valid) {
      console.log('Form submitted with values:', this.loginForm.value);
    } else {
      console.log('Form is invalid');
      this.loginForm.markAllAsTouched();
    }
  }


}
