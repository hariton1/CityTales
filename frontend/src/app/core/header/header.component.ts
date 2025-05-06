import { ChangeDetectionStrategy, Component } from '@angular/core';
import {TuiButton, TuiIcon} from '@taiga-ui/core';
import {TuiSegmented} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';

@Component({
  selector: 'app-header',
  imports: [
    TuiButton,
    TuiSegmented,
    TuiLogoComponent,
    TuiHeaderComponent,
    TuiIcon,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {

}
