import {Component, inject, OnInit} from '@angular/core';
import {HeaderComponent} from './core/header/header.component';
import {MobileNavigationComponent} from './core/mobile-navigation/mobile-navigation.component';
import { RouterOutlet } from '@angular/router';
import {TuiRoot, TuiAlertService} from '@taiga-ui/core';
import {UserService} from './services/user.service';
import {UserLocationService} from './services/user-location.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TuiRoot, HeaderComponent, MobileNavigationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit{
  title = 'frontend';

  private readonly alerts = inject(TuiAlertService);
  private readonly userService = inject(UserService);

  constructor(readonly userLocationService: UserLocationService) {
  }

  ngOnInit() {
    this.userLocationService.startTracking();


    this.preloadRoutes();
  }

  preloadRoutes() {
    console.log('preload routes!')
    import('./core/about/about.component');
  }
}
