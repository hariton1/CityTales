import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FriendsService} from '../../user_db.services/friends.service';
import {FriendsDto} from '../../user_db.dto/friends.dto';
import {UserService} from '../../user_db.services/user.service';

interface Friend {
  email: string;
  becameFriendsOn: Date;
}

@Component({
  selector: 'app-friend-list',
  imports: [CommonModule],
  templateUrl: './friend-list.component.html',
  styleUrl: './friend-list.component.scss'
})
export class FriendListComponent implements OnInit{

  constructor(private friendsService: FriendsService, private userService: UserService) {
  }

  ngOnInit() {
    this.retrieveUserID()
    console.log(this.userId);
    this.retrieveFriends();
  }

  userId: string | null = null;
  friendInfoList: Friend[] = [];

  retrieveUserID(): void {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }

  retrieveFriends(): void {
    if (this.userId == null) {
      return;
    }

    this.friendsService.getFriendsByFriendOne(this.userId).subscribe({
      next: (dtoList: FriendsDto[]) => {

        dtoList.forEach(friendDto => {
          const friendUUID = friendDto.friend_two;

          this.userService.getUserById(friendUUID).subscribe({
            next: (user) => {
              const friend: Friend = {
                email: user.email,
                becameFriendsOn: friendDto.cre_dat
              };
              this.friendInfoList.push(friend);
            },
            error: (err) => {
              console.error(`Error fetching user ${friendUUID}:`, err);
            }
          });
        });
      },
      error: (err) => {
        console.error('Error loading friends:', err);
      }
    });
  }
}

