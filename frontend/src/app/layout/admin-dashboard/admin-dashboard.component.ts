import { Component } from '@angular/core';
import {FormsModule} from "@angular/forms";
import {TuiButton, TuiTitle} from "@taiga-ui/core";
import {TuiHeader} from "@taiga-ui/layout";
import {Router} from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
    imports: [
        FormsModule,
        TuiButton,
        TuiHeader,
        TuiTitle
    ],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent {

  constructor(private router: Router) {
  }

  handleUserManagementClick () {
    this.router.navigate(['/admin/users']);
  }

  handleFeedbacksClick () {
    this.router.navigate(['/feedbacks']);
  }

  handleUserActivityClick () {
    this.router.navigate(['/history']);
  }

}
