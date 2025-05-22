import {Component, OnInit} from '@angular/core';
import {
  TuiAutoColorPipe,
  TuiButton,
  TuiDropdownDirective, TuiDropdownOpen,
  TuiDropdownOptionsDirective,
  TuiInitialsPipe,
  TuiLink, TuiTitle
} from '@taiga-ui/core';
import {TuiAvatar, TuiBadge, TuiItemsWithMoreComponent, TuiMore, TuiStatus} from '@taiga-ui/kit';
import {TuiCell} from '@taiga-ui/layout';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {TuiTable} from '@taiga-ui/addon-table';
import {TuiItem} from '@taiga-ui/cdk';
import {RouterLink} from '@angular/router';
import {UserService} from '../../../user_db.services/user.service';
import {UserDto} from '../../../user_db.dto/user.dto';

@Component({
  selector: 'app-user-list',
  imports: [
    TuiInitialsPipe,
    TuiAutoColorPipe,
    TuiAvatar,
    TuiCell,
    TuiStatus,
    TuiItemsWithMoreComponent,
    TuiBadge,
    TuiDropdownDirective,
    TuiLink,
    NgForOf,
    NgIf,
    TuiTable,
    TuiItem,
    TuiButton,
    TuiMore,
    TuiDropdownOptionsDirective,
    TuiDropdownOpen,
    TuiTitle,
    RouterLink,
    DatePipe
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {

  protected users : UserDto[];

  constructor(readonly userService: UserService) {
    this.users = [];
  }

  ngOnInit(): void {
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

  protected readonly data = [
    {
      id: 1,
      role: {
        roleName: 'Moderator'
      },
      userData: {
        firstName: 'John',
        lastName: 'Cleese',
        email: 'silly@walk.uk',
      },
      status: {
        value: 'Active',
        color: 'var(--tui-status-positive)',
      },
      interests: ['Some', 'items', 'displayed', 'here', 'and', 'can', 'overflow'],
      createdAt: '2021-01-01'
    },
    {
      id: 2,
      role: {
        roleName: 'User'
      },
      userData: {
        firstName: 'Eric',
        lastName: 'Idle',
        email: 'cool@dude.com',
      },
      status: {
        value: 'Blocked',
        color: 'var(--tui-status-negative)',
      },
      interests: ['One', 'Item'],
      createdAt: '2024-02-23'
    },
    {
      id:3,
      role: {
        roleName: 'Contributor'
      },
      userData: {
        firstName: 'Michael',
        lastName: 'Palin',
        email: 'its@man.com',
      },
      status: {
        value: 'Active',
        color: 'var(--tui-status-positive)',
      },
      interests: [],
      createdAt: '2024-07-16'
    },
  ];
}
