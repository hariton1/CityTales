import {Component, OnInit} from '@angular/core';
import {UserService} from '../../user_db.services/user.service';
import {UserDto} from '../../user_db.dto/user.dto';
import {CommonModule, DatePipe} from '@angular/common';
import {FriendsService} from '../../user_db.services/friends.service';
import {FriendsDto} from '../../user_db.dto/friends.dto';
import {UUID} from 'node:crypto';
import {TuiTable} from '@taiga-ui/addon-table';
import {TuiAutoColorPipe, TuiButton, TuiInitialsPipe, TuiTitle} from '@taiga-ui/core';
import {TuiAvatar, TuiStatus} from '@taiga-ui/kit';
import {TuiCell} from '@taiga-ui/layout';

@Component({
  selector: 'app-search-users',
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
  templateUrl: './search-users.component.html',
  styleUrl: './search-users.component.scss'
})
export class SearchUsersComponent implements OnInit{

  protected users : UserDto[];
  userId: string | null = null;

  constructor(private userService: UserService, private friendsService: FriendsService) {
    this.users = [];
  }
  ngOnInit() {
    this.retrieveUserID();
    this.retrieveUsers();
  }

  retrieveUserID(): void {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }

  retrieveUsers() {
    this.userService.getAllUsers()
      .subscribe({
        next: (users) => {
          this.users = users;
        },
        error(error) {
          console.error('Error fetching users:', error);
        },
        complete() {
          console.log('Users fetched successfully!');
        }
      });
  }

  sendFriendInvite(user: UserDto): void {
    if (!this.userId) {
      console.error('User ID not loaded');
      return;
    }

    const invite = new FriendsDto(
      0, // ID, assuming the backend will generate it
      this.userId as UUID,
      user.id,
      new Date()
    );

    this.friendsService.createNewFriendsPair(invite).subscribe({
      next: () => {
        console.log(`Friend invite sent to ${user.email}`);
      },
      error: (err) => {
        console.error('Failed to send friend invite:', err);
      }
    });
  }
}
