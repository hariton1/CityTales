import {Component, OnInit} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FriendsService} from '../../user_db.services/friends.service';
import {FriendsDto} from '../../user_db.dto/friends.dto';
import {UserService} from '../../user_db.services/user.service';
import {UserDto} from '../../user_db.dto/user.dto';
import {TuiTable} from '@taiga-ui/addon-table';
import {TuiAutoColorPipe, TuiButton, TuiInitialsPipe, TuiTitle} from '@taiga-ui/core';
import {TuiAvatar, TuiStatus} from '@taiga-ui/kit';
import {TuiCell} from '@taiga-ui/layout';
import {UUID} from 'node:crypto';

interface Friend {
  email: string;
  becameFriendsOn: Date | null;
  id: UUID;
}

@Component({
  selector: 'app-friend-list',
  standalone: true,
  imports: [CommonModule,
    TuiTable,
    TuiInitialsPipe,
    TuiAutoColorPipe,
    TuiStatus,
    TuiButton,
    TuiTitle,
    TuiInitialsPipe,
    TuiCell,
    TuiAvatar,
    DatePipe],
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
            becameFriendsOn: this.sanitizeDate(sentDto.cre_dat),
            id: user.id
          };
          console.log(sentDto.cre_dat)
          this.friendInfoList.push(friend);
        },
        error: (err) => {
          console.error(`Error fetching user ${friendId}:`, err);
        }
      });
    });
  }

  sanitizeDate(raw: string | Date): Date | null {
    if (raw instanceof Date) return raw;

    const cleaned = raw.replace(/\s?(am|pm)/, '');
    const date = new Date(cleaned);
    return isNaN(date.getTime()) ? null : date;
  }

  removeFriend(friend: Friend) {
    console.log(friend)

    const del1 = this.receivedMap.get(friend.id);
    const del2 = this.sentMap.get(friend.id);

    del1!.cre_dat = this.sanitizeDate(del1!.cre_dat)!;
    del2!.cre_dat = this.sanitizeDate(del2!.cre_dat)!;

    this.friendsService.deleteFriendsPair(del1!).subscribe({
      next: () => console.log('One way: Deleted successfully'),
      error: (err) => console.error('Error deleting:', err)
      });
    this.friendsService.deleteFriendsPair(del2!).subscribe({
      next: () => console.log('Two way: Deleted successfully'),
      error: (err) => console.error('Error deleting:', err)
    });
  }
}
