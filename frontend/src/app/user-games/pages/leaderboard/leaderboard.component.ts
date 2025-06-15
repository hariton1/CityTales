import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import { CommonModule } from '@angular/common';
import {TuiSwitch, tuiSwitchOptionsProvider} from '@taiga-ui/kit';
import {TuiSizeS} from '@taiga-ui/core';
import {FormsModule} from '@angular/forms';
import {UserPointsService} from '../../../user_db.services/user-points.service';
import {forkJoin, map, switchMap} from 'rxjs';
import {UserService} from '../../../user_db.services/user.service';
import {FriendsService} from '../../../user_db.services/friends.service';
import {UserPointDto} from '../../../user_db.dto/user-point.dto';
import {UserDto} from '../../../user_db.dto/user.dto';


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
  constructor(
    private userPointsService: UserPointsService,
    private userService: UserService,
    private friendsService: FriendsService) {
  }

  isFriends = false;
  userId: string | null = null;

  globalLeaderboard: any[] = [];
  friendsLeaderboard: any[] = [];
  currentUserEmail!: string;
  allUserPoints: UserPointDto[] = [];
  allUsers: UserDto[] = [];

  ngOnInit() {
    this.retrieveUserID();

    if (this.userId) {
      this.userService.getUserById(this.userId).subscribe(user => {
        this.currentUserEmail = user.email;
      });
    }

    forkJoin({
      pointsArray: this.userPointsService.getAllUserPoints(),
      userArray: this.userService.getAllUsers()
    }).subscribe(({ pointsArray, userArray }) => {
      this.allUserPoints = pointsArray;
      this.allUsers = userArray;

      // Map user_id to email
      const idToEmailMap = new Map<string, string>(
        userArray
          .filter(user => typeof user.email === 'string' && user.email.trim() !== '')
          .map(user => [user.id, user.email])
      );

      // Accumulate points per email
      const pointsMap = new Map<string, number>();
      pointsArray.forEach(point => {
        const email = idToEmailMap.get(point.user_id);
        if (!email) return;

        const currentPoints = pointsMap.get(email) || 0;
        pointsMap.set(email, currentPoints + point.points);
      });

      // Global leaderboard
      this.globalLeaderboard = Array.from(pointsMap.entries())
        .map(([email, totalPoints]) => ({
          name: email,
          points: totalPoints
        }))
        .sort((a, b) => b.points - a.points);

      // Now build the friends leaderboard
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

              // Use the idToEmailMap to get friends' emails
              const friendEmails = confirmedIds
                .map(id => idToEmailMap.get(id))
                .filter((email): email is string => typeof email === 'string');

              // Add current user’s email
              const emailSet = new Set(friendEmails);
              emailSet.add(this.currentUserEmail);

              // Filter global leaderboard
              this.friendsLeaderboard = this.globalLeaderboard.filter(entry =>
                emailSet.has(entry.name)
              );
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
