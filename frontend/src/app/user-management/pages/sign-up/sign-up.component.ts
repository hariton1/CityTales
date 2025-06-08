import { Component } from '@angular/core';
import {TuiButton, TuiError, TuiLabel, TuiTextfieldComponent, TuiTextfieldDirective, TuiTitle, TuiIcon} from '@taiga-ui/core';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TuiFieldErrorPipe, TuiPassword, TuiTooltip} from '@taiga-ui/kit';
import {AsyncPipe} from '@angular/common';
import {TuiInputModule, TuiInputDateModule} from '@taiga-ui/legacy';
import {TuiDay} from '@taiga-ui/cdk';
import { supabase } from '../../supabase.service';
import {Router} from '@angular/router';
import {UserDataService} from '../../../user_db.services/user-data.service';
import {UserDataDto} from '../../../user_db.dto/user-data.dto';
import {UUID} from 'node:crypto';
import {UserService} from '../../../user_db.services/user.service';


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
  constructor(private router: Router,
              private userDataService: UserDataService) {}

  async onSubmit(): Promise<void> {
    if (this.signupForm.invalid) {
    this.signupForm.markAllAsTouched();
    return;
  }
  const { email, passwordValue, firstName, lastName, birthday } = this.signupForm.value;
  console.log('About to call Supabase:', email, passwordValue, firstName, lastName, birthday);

  const { data, error } = await supabase.auth.signUp({
    email: email!,
    password: passwordValue!,
    options: {
      data: {
        first_name: firstName,
        last_name: lastName,
        birthday: birthday?.toString() // or birthday?.toLocalNativeDate().toISOString() for better format
      }
    }
  });

  console.log('Supabase result:', { data, error });

  if (error) {
    alert(error.message);
    return;
  }
  alert('Signup successful! Please check your email to confirm your account.');
  this.userDataService.saveUserData(new UserDataDto(
    -1,
    data.user?.id as UUID,
    'User',
    'Active',
    new Date()
  )).subscribe(data => {
    console.log(data);
  })
  this.router.navigate(['/login']);
  }
}

