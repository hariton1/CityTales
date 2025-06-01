import {Component, OnInit} from '@angular/core';
import {UserService} from '../../user_db.services/user.service';
import {UserDto} from '../../user_db.dto/user.dto';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-search-users',
  imports: [CommonModule],
  templateUrl: './search-users.component.html',
  styleUrl: './search-users.component.scss'
})
export class SearchUsersComponent implements OnInit{

  protected users : UserDto[];

  constructor(private userService: UserService) {
    this.users = [];
  }
  ngOnInit() {
    this.retrieveUsers();
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
    console.log(`Sending friend invite to ${user.email}`);
    // Implement the real logic here, e.g., via userService.sendFriendInvite(user.id)
  }
}
