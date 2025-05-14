import {Component, OnInit} from '@angular/core';
import {AsyncPipe} from '@angular/common';
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
import {ActivatedRoute, ParamMap, RouterLink} from '@angular/router';

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
  ],
  templateUrl: './edit-user.component.html',
  styleUrl: './edit-user.component.scss'
})
export class EditUserComponent implements OnInit{
  userId: number | null = null;
  loadUser: any;
  protected persons = ['User', 'Moderator', 'Contributor'];
  protected statusValues = ['Active', 'Blocked'];
  // Use the parse method directly
  accountCreated: any = null;

  protected editUserForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl({value: '', disabled: true}, Validators.required),
    role: new FormControl('', Validators.required),
    status: new FormControl('', Validators.required),
    birthday: new FormControl({value: TuiDay.fromLocalNativeDate(new Date()), disabled: true}, Validators.required)
  });

  constructor(readonly route: ActivatedRoute) {
  }

  ngOnInit(): void {
    // Method 1: Simple approach
    this.route.paramMap.subscribe((params: ParamMap) => {
      const idParam = params.get('id');
      if (idParam) {
        this.userId = +idParam;
        this.loadUser = this.data.find(user => user.id === this.userId);// Convert string to number using the '+' operator
      }

      if (this.loadUser) {
        this.editUserForm.patchValue({
          firstName: this.loadUser.userData.firstName,
          lastName: this.loadUser.userData.lastName,
          email: this.loadUser.userData.email,
          role: this.loadUser.role.roleName,
          status: this.loadUser.status.value,
          birthday: TuiDay.jsonParse(this.loadUser.userData.birthday)
        });
        this.accountCreated = TuiDay.parseRawDateString(this.loadUser.createdAt, 'YMD');
      }
    });
  }

  getMonthName(dateNumber: number): string {
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return monthNames[dateNumber];
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
}
