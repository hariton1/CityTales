import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {TuiSwitch, tuiSwitchOptionsProvider} from '@taiga-ui/kit';
import {TuiSizeS} from '@taiga-ui/core';
import {FormsModule} from '@angular/forms';
import {UserPointsService} from '../../../user_db.services/user-points.service';
import {forkJoin, map, switchMap} from 'rxjs';
import {UserService} from '../../../user_db.services/user.service';
import {FriendsService} from '../../../user_db.services/friends.service';


@Component({
  selector: 'app-leaderboard',
  imports: [CommonModule, TuiSwitch, FormsModule],
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.scss'],
  providers: [
    tuiSwitchOptionsProvider({showIcons: false, appearance: () => 'primary'}),
  ],
})
export class LeaderboardComponent implements OnInit{
  constructor(private userPointsService: UserPointsService, private userService: UserService, private friendsService: FriendsService) {
  }

  isFriends = false;
  userId: string | null = null;

  globalLeaderboard: any[] = [];
  friendsLeaderboard: any[] = [];
  currentUserEmail!: string;

  ngOnInit() {

    this.retrieveUserID();
    if (this.userId) {
      this.userService.getUserById(this.userId).subscribe(user => {
        this.currentUserEmail = user.email;
      });
    }

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

        if (!this.userId || !this.currentUserEmail) return;

        const sentMap = new Map<string, any>();
        const receivedMap = new Map<string, any>();

        this.friendsService.getFriendsByFriendOne(this.userId).subscribe({
          next: sentList => {
            sentList.forEach(dto => sentMap.set(dto.friend_two, dto));

            this.friendsService.getFriendsByFriendTwo(this.userId!).subscribe({
              next: receivedList => {
                receivedList.forEach(dto => receivedMap.set(dto.friend_one, dto));

                const confirmedIds = [...sentMap.keys()].filter(id =>
                  receivedMap.has(id)
                );

                const friendEmailRequests = confirmedIds.map(id =>
                  this.userService.getUserById(id).pipe(
                    map(user => user.email)
                  )
                );

                forkJoin(friendEmailRequests).subscribe(friendEmails => {
                  const emailSet = new Set(friendEmails);
                  emailSet.add(this.currentUserEmail); // Ensure current user is included

                  this.friendsLeaderboard = globalLeaderboard.filter(entry =>
                    emailSet.has(entry.name)
                  );
                });
              },
              error: err => console.error('Error fetching received invites:', err)
            });
          },
          error: err => console.error('Error fetching sent invites:', err)
        });
      });
  }

  retrieveUserID(): void {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }

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
