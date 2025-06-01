import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserDto} from '../../user_db.dto/user.dto';
import {FriendsDto} from '../../user_db.dto/friends.dto';
import {FriendsService} from '../../user_db.services/friends.service';
import {UserService} from '../../user_db.services/user.service';

interface Invite {
  user: UserDto;
  since: Date;
}

@Component({
  selector: 'app-invite-users',
  imports: [CommonModule],
  templateUrl: './invite-users.component.html',
  styleUrl: './invite-users.component.scss'
})
export class InviteUsersComponent implements OnInit{

  userId: string | null = null;
  confirmedFriends: Invite[] = [];
  sentInvites: Invite[] = [];
  receivedInvites: Invite[] = [];

  private sentInviteMap = new Map<string, FriendsDto>();
  private receivedInviteMap = new Map<string, FriendsDto>();

  constructor(private friendsService: FriendsService, private userService: UserService) {
  }
  ngOnInit() {
    this.retrieveUserID();
    if (this.userId) {
      this.loadFriendshipData();
    }
  }

  retrieveUserID(): void {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }

  loadFriendshipData(): void {
    this.retrieveSentInvites(() => {
      this.retrieveReceivedInvites(() => {
        this.resolveFriendshipStates();
      });
    });
  }

  retrieveSentInvites(callback: () => void): void {
    if (!this.userId) return;

    this.friendsService.getFriendsByFriendOne(this.userId).subscribe({
      next: (sentList: FriendsDto[]) => {
        sentList.forEach(dto => this.sentInviteMap.set(dto.friend_two, dto));
        callback();
      },
      error: (err) => console.error('Error loading sent invites:', err)
    });
  }

  retrieveReceivedInvites(callback: () => void): void {
    if (!this.userId) return;

    this.friendsService.getFriendsByFriendTwo(this.userId).subscribe({
      next: (receivedList: FriendsDto[]) => {
        receivedList.forEach(dto => this.receivedInviteMap.set(dto.friend_one, dto));
        callback();
      },
      error: (err) => console.error('Error loading received invites:', err)
    });
  }

  resolveFriendshipStates(): void {
    if (!this.userId) return;

    const allUUIDs = new Set([
      ...this.sentInviteMap.keys(),
      ...this.receivedInviteMap.keys()
    ]);

    allUUIDs.forEach((otherUserId) => {
      const sent = this.sentInviteMap.get(otherUserId);
      const received = this.receivedInviteMap.get(otherUserId);

      this.userService.getUserById(otherUserId).subscribe({
        next: (user: UserDto) => {
          if (sent && received) {
            // Confirmed friend
            this.confirmedFriends.push({user, since: sent.cre_dat});
          } else if (sent) {
            // Sent invite
            this.sentInvites.push({user, since: sent.cre_dat});
          } else if (received) {
            // Received invite
            this.receivedInvites.push({user, since: received.cre_dat});
          }
        },
        error: (err) => {
          console.error(`Error loading user ${otherUserId}:`, err);
        }
      });
    });
  }
}
