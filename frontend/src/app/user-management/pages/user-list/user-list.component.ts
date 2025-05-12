import { Component } from '@angular/core';
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
import {NgForOf, NgIf} from '@angular/common';
import {TuiTable} from '@taiga-ui/addon-table';
import {TuiItem} from '@taiga-ui/cdk';
import {RouterLink} from '@angular/router';

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
    RouterLink
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {
  protected readonly data = [
    {
      id: 1,
      role: {
        roleName: 'Moderator'
      },
      userData: {
        name: 'John Cleese',
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
        name: 'Eric Idle',
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
        name: 'Michael Palin',
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
