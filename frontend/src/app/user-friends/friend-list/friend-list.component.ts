import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FriendsService} from '../../user_db.services/friends.service';
import {FriendsDto} from '../../user_db.dto/friends.dto';
import {UserService} from '../../user_db.services/user.service';
import {UserDto} from '../../user_db.dto/user.dto';

interface Friend {
  email: string;
  becameFriendsOn: Date;
}

@Component({
  selector: 'app-friend-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './friend-list.component.html',
  styleUrl: './friend-list.component.scss'
})
export class FriendListComponent implements OnInit {

  userId: string | null = null;
  friendInfoList: Friend[] = [];

  private sentMap = new Map<string, FriendsDto>();
  private receivedMap = new Map<string, FriendsDto>();

  constructor(
    private friendsService: FriendsService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.retrieveUserID();
    if (this.userId) {
      this.loadFriendData();
    }
  }

  retrieveUserID(): void {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }

  loadFriendData(): void {
    if (!this.userId) return;

    this.friendsService.getFriendsByFriendOne(this.userId).subscribe({
      next: (sentList: FriendsDto[]) => {
        sentList.forEach(dto => this.sentMap.set(dto.friend_two, dto));

        this.friendsService.getFriendsByFriendTwo(this.userId!).subscribe({
          next: (receivedList: FriendsDto[]) => {
            receivedList.forEach(dto => this.receivedMap.set(dto.friend_one, dto));
            this.buildConfirmedFriends();
          },
          error: err => console.error('Error fetching received invites:', err)
        });
      },
      error: err => console.error('Error fetching sent invites:', err)
    });
  }

  buildConfirmedFriends(): void {
    const confirmedIds = [...this.sentMap.keys()].filter(id => this.receivedMap.has(id));

    confirmedIds.forEach(friendId => {
      const sentDto = this.sentMap.get(friendId)!;

      this.userService.getUserById(friendId).subscribe({
        next: (user: UserDto) => {
          const friend: Friend = {
            email: user.email,
            becameFriendsOn: sentDto.cre_dat
          };
          this.friendInfoList.push(friend);
        },
        error: (err) => {
          console.error(`Error fetching user ${friendId}:`, err);
        }
      });
    });
  }
}
