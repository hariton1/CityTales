import {Component, inject} from '@angular/core';
import {HeaderComponent} from './core/header/header.component';
import {MobileNavigationComponent} from './core/mobile-navigation/mobile-navigation.component';
import { RouterOutlet } from '@angular/router';
import {TuiRoot, TuiAlertService, TuiButton} from '@taiga-ui/core';
import {UserService} from './services/user.service';
import {SERVER_CONNECTION_TEST_STRING} from './globals';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TuiRoot, HeaderComponent, MobileNavigationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';

  private readonly alerts = inject(TuiAlertService);
  private readonly userService = inject(UserService);
}
