import {ChangeDetectionStrategy, Component} from '@angular/core';
import {NgForOf} from '@angular/common';
import {TuiTabBar} from '@taiga-ui/addon-mobile';

@Component({
  selector: 'app-mobile-navigation',
  imports: [
    NgForOf,
    TuiTabBar
  ],
  templateUrl: './mobile-navigation.component.html',
  styleUrl: './mobile-navigation.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MobileNavigationComponent {
  protected activeItemIndex = 0;

  protected readonly items = [
    {
      text: 'Explore',
      icon: '@tui.earth'
    },
    {
      text: 'Tours',
      icon: '@tui.compass',
    },
    {
      text: 'Contribute',
      icon: '@tui.hand-helping',
    },
    {
      text: 'Login',
      icon: '@tui.log-in',
    },
    {
      text: 'More',
      icon: '@tui.ellipsis',
      badge: 15
    },
  ];
}
