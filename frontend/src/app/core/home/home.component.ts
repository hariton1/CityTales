import { Component } from '@angular/core';
import {TuiHeader} from '@taiga-ui/layout';
import {TuiButton} from '@taiga-ui/core';

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

  constructor(private router: any) {
  }

  handleClick () {
    this.router.navigate(['/register']);
  }
}
