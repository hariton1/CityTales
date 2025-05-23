import {Component, OnInit} from '@angular/core';
import {AsyncPipe, NgIf} from '@angular/common';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {
  TuiButton,
  TuiError,
  TuiIcon,
  TuiLabel,
  TuiTextfieldComponent,
  TuiTextfieldDirective, TuiTextfieldDropdownDirective, TuiTextfieldOptionsDirective,
  TuiTitle
} from '@taiga-ui/core';
import {
  TuiChevron,
  TuiDataListWrapperComponent,
  TuiFieldErrorPipe,
  TuiSelectDirective,
} from '@taiga-ui/kit';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {TuiInputDateModule, TuiInputDateRangeModule} from '@taiga-ui/legacy';
import {TuiDay} from '@taiga-ui/cdk';
import {ActivatedRoute, ParamMap, RouterLink, Router} from '@angular/router';
import {UserService} from '../../../user_db.services/user.service';
import {UserDto} from '../../../user_db.dto/user.dto';

@Component({
  selector: 'app-edit-user',
  imports: [
    AsyncPipe,
    ReactiveFormsModule,
    TuiButton,
    TuiError,
    TuiFieldErrorPipe,
    TuiForm,
    TuiHeader,
    TuiIcon,
    TuiInputDateModule,
    TuiInputDateRangeModule,
    TuiLabel,
    TuiTextfieldComponent,
    TuiTextfieldDirective,
    TuiTitle,
    TuiDataListWrapperComponent,
    TuiSelectDirective,
    TuiChevron,
    TuiTextfieldOptionsDirective,
    TuiTextfieldDropdownDirective,
    RouterLink,
    NgIf,
  ],
  templateUrl: './edit-user.component.html',
  styleUrl: './edit-user.component.scss'
})
export class EditUserComponent implements OnInit {
  userId: string | null = null;
  user: UserDto | null = null;
  protected persons = ['User', 'Moderator', 'Contributor'];
  protected statusValues = ['Active', 'Blocked'];
  // Use the parse method directly
  accountCreated: any = null;

  constructor(readonly route: ActivatedRoute, readonly userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
    // Method 1: Simple approach
    this.route.paramMap.subscribe((params: ParamMap) => {
      const idParam = params.get('id');
      if (idParam) {
        this.userId = idParam;
        this.getUserById(idParam);
      }
    });
  }

  getUserById(id: string): void {
    this.userService.getUserById(id)
      .subscribe(user => {
        this.user = user;
        console.log('User loaded: ' + this.user);
          this.editUserForm.patchValue({
            displayName: this.user.display_name,
            email: this.user.email,
            role: 'User', /* to be replaced with the actual role */
            status: this.user.is_active ? 'Active' : 'Blocked',
            birthday: TuiDay.currentLocal() /* to be replaced with the actual birthday */
          });
        const createdDate = new Date(this.user.created_at);
        this.accountCreated = new TuiDay(
          createdDate.getFullYear(),
          createdDate.getMonth(), // Note: JavaScript months are 0-indexed
          createdDate.getDate()
        );

      })
  }

  getMonthName(dateNumber: number): string {
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return monthNames[dateNumber];
  }

  onSubmit(): void {
    if (this.editUserForm.valid && this.user) {
      const formValues = this.editUserForm.getRawValue();

      const updatedUser = new UserDto(
        this.user.id,
        this.user.supabase_id,
        formValues.email ?? '',
        this.user.created_at,
        formValues.displayName ?? '',
        formValues.status === 'Active',
      );

      this.userService.updateUser(updatedUser)
        .subscribe({
          next: () => {
            console.log('User updated successfully');
            this.router.navigate(['admin/users']);
          },
          error: (error) => {
            console.error('Error updating user:', error);
          }
        });
    }
  }

  protected editUserForm = new FormGroup({
    displayName: new FormControl('', Validators.required),
    email: new FormControl({value: '', disabled: true}, Validators.required),
    role: new FormControl('', Validators.required),
    status: new FormControl('', Validators.required),
    birthday: new FormControl({value: TuiDay.fromLocalNativeDate(new Date()), disabled: true}, Validators.required)
  });
}


/*
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
        birthday: '1995-05-22'
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
        birthday: '1995-05-22'
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
        birthday: '1995-05-22'
      },
      status: {
        value: 'Active',
        color: 'var(--tui-status-positive)',
      },
      interests: [],
      createdAt: '2024-07-16'
    },
  ];
  protected readonly Date = Date;
*/
