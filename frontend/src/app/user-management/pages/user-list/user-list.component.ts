import {Component, inject, OnInit} from '@angular/core';
import {
  TuiAlertService,
  TuiAutoColorPipe,
  TuiButton,
  TuiInitialsPipe,
  TuiTitle
} from '@taiga-ui/core';
import {
  TUI_CONFIRM,
  TuiAvatar,
  TuiConfirmData,
  TuiStatus
} from '@taiga-ui/kit';
import {TuiCell} from '@taiga-ui/layout';
import {DatePipe, NgForOf} from '@angular/common';
import {TuiTable} from '@taiga-ui/addon-table';
import {RouterLink} from '@angular/router';
import {UserService} from '../../../user_db.services/user.service';
import {UserDto} from '../../../user_db.dto/user.dto';
import {TuiResponsiveDialogService} from '@taiga-ui/addon-mobile';
import {switchMap} from 'rxjs';
import {TuiThemeColorService} from '@taiga-ui/cdk';

@Component({
  selector: 'app-user-list',
  imports: [
    TuiInitialsPipe,
    TuiAutoColorPipe,
    TuiAvatar,
    TuiCell,
    TuiStatus,
    NgForOf,
    TuiTable,
    TuiButton,
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
  private readonly theme = inject(TuiThemeColorService);
  private alerts = inject(TuiAlertService);

  ngOnInit(): void {
    this.theme.color = '#000000';
    this.userService.getAllUsers()
      .subscribe({
        next: (users) => {
          this.users = users.filter(user => user.status !== 'Deleted');
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
    const email = user.email;
    this.confirmDelete(id,email);
  }

  protected confirmDelete(userId: string,email: string): void {
    console.log(userId);
    const data: TuiConfirmData = {
      content:
        '<strong> ' + email +' </strong> will be deleted. This action cannot be undone!',
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
      .subscribe({
        next: () => {
          window.location.reload();
        },
        error: (error) => {
          console.error('Error deleting users:', error);
        },
        complete: () => {
          window.location.reload();
        }
      });
  }

  deleteUser(id: string) : void {
    this.userService.deleteUserById(id).subscribe({});
  }
}
