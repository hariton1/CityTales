import { Component } from '@angular/core';
import {LeaderboardComponent} from '../leaderboard/leaderboard.component';
import {BadgesComponent} from '../badges/badges.component';
import {XpTrackerComponent} from '../xp-tracker/xp-tracker.component';
import {fromEvent, mapTo, merge, Observable, of} from 'rxjs';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-user-games',
  imports: [LeaderboardComponent, BadgesComponent, XpTrackerComponent, CommonModule],
  templateUrl: './user-games.component.html',
  styleUrl: './user-games.component.scss'
})
export class UserGamesComponent {
  online$: Observable<boolean>;

  constructor() {
    this.online$ = merge(
      of(navigator.onLine),
      fromEvent(window, 'online').pipe(mapTo(true)),
      fromEvent(window, 'offline').pipe(mapTo(false))
    );
  }
}
