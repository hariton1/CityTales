import {Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TuiRoot} from '@taiga-ui/core';
import {HeaderComponent} from './core/header/header.component';
import {ExploreLayoutComponent} from './layout/explore-layout/explore-layout.component';
import {MobileNavigationComponent} from './core/mobile-navigation/mobile-navigation.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TuiRoot, HeaderComponent, ExploreLayoutComponent, MobileNavigationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';

}
