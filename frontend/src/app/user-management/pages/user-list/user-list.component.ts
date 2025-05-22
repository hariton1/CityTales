import {Component, inject, OnInit} from '@angular/core';
import {
  TuiAlertService,
  TuiAutoColorPipe,
  TuiButton,
  TuiDropdownDirective, TuiDropdownOpen,
  TuiDropdownOptionsDirective,
  TuiInitialsPipe,
  TuiLink, TuiTitle
} from '@taiga-ui/core';
import {
  TUI_CONFIRM,
  TuiAvatar,
  TuiBadge,
  TuiConfirmData,
  TuiItemsWithMoreComponent,
  TuiMore,
  TuiStatus
} from '@taiga-ui/kit';
import {TuiCell} from '@taiga-ui/layout';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {TuiTable} from '@taiga-ui/addon-table';
import {TuiItem} from '@taiga-ui/cdk';
import {RouterLink} from '@angular/router';
import {UserService} from '../../../user_db.services/user.service';
import {UserDto} from '../../../user_db.dto/user.dto';
import {TuiResponsiveDialogService} from '@taiga-ui/addon-mobile';
import {switchMap} from 'rxjs';
import {UUID} from 'node:crypto';

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

  private dialogs = inject(TuiResponsiveDialogService);
  private alerts = inject(TuiAlertService);

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

  protected handleDeleteClick(user: UserDto): void {
    // Use getters to ensure we get the values correctly
    const id = user.id;
    const displayName = user.display_name;
    this.confirmDelete(id, displayName);
  }

  protected confirmDelete(userId: string, displayName: string): void {
    console.log(userId);
    console.log(displayName);
    const data: TuiConfirmData = {
      content:
        '<strong> ' + displayName  +' </strong> will be deleted. This action cannot be undone!',
      yes: 'Yes',
      no: 'No',
    };

    this.dialogs
      .open<boolean>(TUI_CONFIRM, {
        label: 'Are you sure you want to delete the user?',
        size: 'm',
        data,
      })
      .pipe(
        switchMap((response) => {
          if (response) {
            this.deleteUser(userId);
            return this.alerts.open('User deleted successfully!', {label: 'Success!', appearance: 'success', autoClose: 3000});
          }
          return this.alerts.open('User deletion cancelled!', {label: 'Cancelled!', appearance: 'warning', autoClose: 3000});
        }))
      .subscribe();
  }

  deleteUser(id: string) : void {
    this.userService.deleteUserById(id);
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
