import { Component } from '@angular/core';
import {TuiButton, TuiError, TuiLabel, TuiTextfieldComponent, TuiTextfieldDirective, TuiTitle, TuiIcon} from '@taiga-ui/core';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TuiFieldErrorPipe, TuiPassword, TuiTooltip} from '@taiga-ui/kit';
import {AsyncPipe} from '@angular/common';
import {TuiInputModule} from '@taiga-ui/legacy';
import { supabase } from '../../supabase.service'; // adjust the path if neededimport { supabase } from '../supabase.service'; // adjust the path if needed


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

  async onSubmit(): Promise<void> {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    const { email, passwordValue } = this.loginForm.value;

    // Use Supabase to log in
    const { data, error } = await supabase.auth.signInWithPassword({
      email: email!,
      password: passwordValue!,
    });

    if (error) {
      alert(error.message); // Show error to the user
      return;
    }
    // Login successful, session info in data.session
    alert('Login successful!');
    // Optionally: redirect, store user info, etc.
  }



}
