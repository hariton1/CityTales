import { Component } from '@angular/core';
import {LeaderboardComponent} from '../leaderboard/leaderboard.component';
import {BadgesComponent} from '../badges/badges.component';

@Component({
  selector: 'app-user-games',
  imports: [LeaderboardComponent, BadgesComponent],
  templateUrl: './user-games.component.html',
  styleUrl: './user-games.component.scss'
})
export class UserGamesComponent {

}
