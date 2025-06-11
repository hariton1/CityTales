import { Component } from '@angular/core';
import {TuiButton, TuiError, TuiLabel, TuiTextfieldComponent, TuiTextfieldDirective, TuiTitle, TuiIcon} from '@taiga-ui/core';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TuiFieldErrorPipe, TuiPassword, TuiTooltip} from '@taiga-ui/kit';
import {AsyncPipe} from '@angular/common';
import {TuiInputModule} from '@taiga-ui/legacy';
import { supabase } from '../../supabase.service';
import {Router} from '@angular/router'; // adjust the path if neededimport { supabase } from '../supabase.service'; // adjust the path if needed
import {jwtDecode} from 'jwt-decode';
import {UserService} from '../../../user_db.services/user.service';
import {UserPointsService} from '../../../user_db.services/user-points.service';
import { UserPointDto } from '../../../user_db.dto/user-point.dto';

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
  constructor(private router: Router,
              private userService: UserService,
              private userPointsService: UserPointsService) {}

  async ngOnInit(): Promise<void> {
    const { data: { session } } = await supabase.auth.getSession();
    // If session and token exists, skip login
    if (session && session.access_token) {
      console.log('JWT-Token:', session.access_token);
      this.router.navigate(['/explore']);
    }
  }
  async onSubmit(): Promise<void> {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    const { email, passwordValue } = this.loginForm.value;

    const { data, error } = await supabase.auth.signInWithPassword({
      email: email!,
      password: passwordValue!,
    });

    if (error) {
      alert(error.message);
      return;
    }

    // Login successful, session info in data.session
    alert('Login successful!');
    // --- Log the JWT here ---
    if (data.session) {
      console.log('JWT:', data.session.access_token);

      const token = data.session.access_token;
      const decodedToken: any = jwtDecode(token);

      const uuid = decodedToken.sub;
      console.log('decoded token: ' + decodedToken);
      if (uuid) {
        console.log("user token: " + uuid);
        localStorage.setItem('user_uuid', uuid);
        this.userService.getUserById(uuid).subscribe(user => {
          console.log(user);
          localStorage.setItem('user_role', user.role)
          localStorage.setItem('interest_filtering', 'true')
        });
        this.userPointsService.createNewPoints(new UserPointDto(
          -1,
          uuid,
          1,
          new Date(),
          -100
        )).subscribe(points => {
          console.log(points);
        });
      }
    } else {
      console.log('No session returned from Supabase.');
    }
     this.router.navigate(['/explore']);
  }



}
