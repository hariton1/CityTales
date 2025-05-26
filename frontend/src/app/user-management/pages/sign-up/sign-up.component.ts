import { Component } from '@angular/core';
import {TuiButton, TuiError, TuiLabel, TuiTextfieldComponent, TuiTextfieldDirective, TuiTitle, TuiIcon} from '@taiga-ui/core';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TuiFieldErrorPipe, TuiPassword, TuiTooltip} from '@taiga-ui/kit';
import {AsyncPipe} from '@angular/common';
import {TuiInputModule, TuiInputDateModule} from '@taiga-ui/legacy';
import {TuiDay} from '@taiga-ui/cdk';
import { supabase } from '../../supabase.service';


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

  async onSubmit(): Promise<void> {
    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();
      return;
    }
    const { email, passwordValue, firstName, lastName, birthday } = this.signupForm.value;

    // Use Supabase to sign up
    const { data, error } = await supabase.auth.signUp({
      email: email!,
      password: passwordValue!,
      options: {
        data: {
          first_name: firstName,
          last_name: lastName,
          birthday: birthday?.toString() // Save as string (optional)
        }
      }
    });

    if (error) {
      // Show error to user
      alert(error.message);
      return;
    }
    // Success! Show confirmation or redirect
    alert('Signup successful! Please check your email to confirm your account.');

  }
}

