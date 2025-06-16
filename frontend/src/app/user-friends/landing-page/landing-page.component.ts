import { Component } from '@angular/core';
import {FriendListComponent} from '../friend-list/friend-list.component';
import {CommonModule} from '@angular/common';
import {InviteUsersComponent} from '../invite-users/invite-users.component';
import {SearchUsersComponent} from '../search-users/search-users.component';
import {fromEvent, mapTo, merge, Observable, of} from 'rxjs';
import {BreakpointService} from '../../services/breakpoints.service';

@Component({
  selector: 'app-landing-page',
  imports: [FriendListComponent, InviteUsersComponent, SearchUsersComponent, CommonModule],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {

  online$: Observable<boolean>;

  constructor(readonly breakpointService: BreakpointService) {
    this.online$ = merge(
      of(navigator.onLine),
      fromEvent(window, 'online').pipe(mapTo(true)),
      fromEvent(window, 'offline').pipe(mapTo(false))
    );
  }

  get isMobile(): boolean {
    return ['mobile', 'tablet'].includes(this.breakpointService.currentLevel ?? '');
  }
}
