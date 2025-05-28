import { Component } from '@angular/core';
import {LeaderboardComponent} from '../leaderboard/leaderboard.component';

@Component({
  selector: 'app-user-games',
  imports: [LeaderboardComponent],
  templateUrl: './user-games.component.html',
  styleUrl: './user-games.component.scss'
})
export class UserGamesComponent {

}
