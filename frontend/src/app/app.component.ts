import {Component, inject} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TuiRoot, TuiAlertService, TuiButton} from '@taiga-ui/core';

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

  protected showNotification(): void {
    console.log('show notification');
    this.alerts
      .open('Basic <strong>HTML</strong>', {label: 'With a heading!'})
      .subscribe();
  }
}
