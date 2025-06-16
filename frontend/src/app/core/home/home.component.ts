import { Component } from '@angular/core';
import {TuiHeader} from '@taiga-ui/layout';
import {TuiButton} from '@taiga-ui/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [
    TuiHeader,
    TuiButton
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(private router: Router) {
  }

  handleClick () {
    this.router.navigate(['/first-steps']);
  }
}
