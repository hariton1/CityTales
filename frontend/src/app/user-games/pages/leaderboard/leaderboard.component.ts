import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
@Component({
  selector: 'app-leaderboard',
  imports: [CommonModule],
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.scss'
})
export class LeaderboardComponent {
  leaderboard = [
    { name: 'Zane', points: 950 },
    { name: 'Alice', points: 920 },
    { name: 'Yuki', points: 870 },
    { name: 'Max', points: 810 },
    { name: 'Nina', points: 790 },
  ];
}
