import { Component } from '@angular/core';
import {TuiButton, TuiError, TuiLabel, TuiTextfieldComponent, TuiTextfieldDirective, TuiTitle, TuiIcon} from '@taiga-ui/core';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TuiFieldErrorPipe, TuiPassword, TuiTooltip} from '@taiga-ui/kit';
import {AsyncPipe} from '@angular/common';
import {TuiInputModule, TuiInputDateModule} from '@taiga-ui/legacy';
import {TuiDay} from '@taiga-ui/cdk';

@Component({
  selector: 'app-sign-up',
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
    TuiInputModule,
    TuiInputDateModule
  ],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.scss'
})
export class SignUpComponent {

  protected signupForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    passwordValue: new FormControl('', Validators.required),
    birthday: new FormControl(TuiDay.fromLocalNativeDate(new Date()), Validators.required)
  });

  onSubmit(): void {
    const signupFormValuesJSON = JSON.stringify(this.signupForm.value);
    if (this.signupForm.valid) {
      console.log('Sign-up Form submitted with values:', signupFormValuesJSON);
    } else {
      console.log('Sign-up form is invalid');
      this.signupForm.markAllAsTouched();
    }
  }
}
