import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TuiSwitch, tuiSwitchOptionsProvider} from '@taiga-ui/kit';
import {TuiSizeS} from '@taiga-ui/core';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule, TuiSwitch, FormsModule],
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.scss'],
  providers: [
    tuiSwitchOptionsProvider({showIcons: false, appearance: () => 'primary'}),
  ],
})
export class LeaderboardComponent {
  isFriends = false;

  globalLeaderboard = [
    { name: 'Zane', points: 950 },
    { name: 'Alice', points: 920 },
    { name: 'Yuki', points: 870 },
    { name: 'Max', points: 810 },
    { name: 'Nina', points: 790 },
  ];

  friendsLeaderboard = [
    { name: 'Alice', points: 920 },
    { name: 'Max', points: 810 },
    { name: 'Cara', points: 700 },
  ];

  get leaderboard() {
    return this.isFriends ? this.friendsLeaderboard : this.globalLeaderboard;
  }

  toggleLeaderboard(value: boolean) {
    this.isFriends = value;
  }

  getSize(isLarge: boolean): TuiSizeS {
    return isLarge ? 'm' : 's';
  }
}
