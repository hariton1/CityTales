import {Component, inject, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TuiRoot, TuiAlertService, TuiButton} from '@taiga-ui/core';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TuiRoot, TuiButton],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'frontend';

  private readonly alerts = inject(TuiAlertService);
  private readonly httpClient: HttpClient = inject(HttpClient);

  ngOnInit(): void {
    this.httpClient.get('http://localhost:8090/userInterests', {responseType: "text"}).subscribe(buffer => {
      console.log(buffer);
    });
  }

  protected showNotification(): void {
    console.log('show notification');
    this.alerts
      .open('Basic <strong>HTML</strong>', {label: 'With a heading!'})
      .subscribe();
  }
}
