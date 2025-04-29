import {Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TuiRoot} from '@taiga-ui/core';
import {HeaderComponent} from './core/header/header.component';
import {ExploreLayoutComponent} from './layout/explore-layout/explore-layout.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TuiRoot, HeaderComponent, ExploreLayoutComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';

}
