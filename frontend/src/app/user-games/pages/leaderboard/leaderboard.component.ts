import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {TuiSwitch, tuiSwitchOptionsProvider} from '@taiga-ui/kit';
import {TuiSizeS} from '@taiga-ui/core';
import {FormsModule} from '@angular/forms';
import {UserPointsService} from '../../../user_db.services/user-points.service';
import {forkJoin, map, switchMap} from 'rxjs';
import {UserService} from '../../../user_db.services/user.service';


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
export class LeaderboardComponent implements OnInit{
  constructor(private userPointsService: UserPointsService, private userService: UserService) {
  }

  ngOnInit() {
    this.userPointsService.getAllUserPoints()
      .pipe(
        switchMap(pointsArray => {
          const emailRequests$ = pointsArray.map(point =>
            this.userService.getUserEmailById(point.user_id).pipe(
              map(email => ({
                email,
                points: point.points
              }))
            )
          );
          return forkJoin(emailRequests$);
        }),
        map(entries => {
          const pointsMap = new Map<string, number>();

          entries.forEach(({ email, points }) => {
            const currentPoints = pointsMap.get(email) || 0;
            pointsMap.set(email, currentPoints + points);
          });

          return Array.from(pointsMap.entries())
            .map(([email, totalPoints]) => ({
              name: email,
              points: totalPoints
            }))
            .sort((a, b) => b.points - a.points); // Sort descending by points
        })
      )
      .subscribe(globalLeaderboard => {
        this.globalLeaderboard = globalLeaderboard;
      });
  }




isFriends = false;

  globalLeaderboard: any[] = [];
  friendsLeaderboard: any[] = [];

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
