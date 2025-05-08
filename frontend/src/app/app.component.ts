import {Component, inject} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TuiRoot, TuiAlertService, TuiButton} from '@taiga-ui/core';
import {UserService} from './services/user.service';
import {SERVER_CONNECTION_TEST_STRING} from './globals';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TuiRoot, TuiButton],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';

  private readonly alerts = inject(TuiAlertService);
  private readonly userService = inject(UserService);

  protected testConnection(): void {
    console.log('Test Connection');
    let testConnection = this.userService.testConnection();

    testConnection.subscribe(testString => {
      if (testString) {
        if (testString === SERVER_CONNECTION_TEST_STRING) {
          this.alerts
            .open(testString, {label: 'Success!'})
            .subscribe();
        } else {
          this.alerts
            .open('Something went wrong!', {label: 'Failure!'})
            .subscribe();
        }
      }
    });
  }
}
