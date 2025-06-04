import { Component } from '@angular/core';
import {AsyncPipe, NgIf} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ActivatedRoute, ParamMap, Router, RouterLink} from '@angular/router';
import {
  TuiButton,
  TuiError,
  TuiIcon,
  TuiLabel,
  TuiTextfieldComponent,
  TuiTextfieldDirective,
  TuiTitle
} from '@taiga-ui/core';
import {TuiChevron, TuiDataListWrapperComponent, TuiFieldErrorPipe, TuiSelectDirective} from '@taiga-ui/kit';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {TuiDay} from '@taiga-ui/cdk';
import {UserService} from '../../../user_db.services/user.service';
import {UserDto} from '../../../user_db.dto/user.dto';

@Component({
  selector: 'app-user-profile',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    TuiButton,
    TuiHeader,
    TuiIcon,
    TuiLabel,
    TuiTextfieldComponent,
    TuiTextfieldDirective,
    TuiTitle
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent {

  userId: string | null = null;
  user: UserDto | null = null;
  accountCreated: any = null;

  constructor(readonly route: ActivatedRoute,readonly userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
    console.log(`Processing feedback for wikiId: ${this.userId}`);
    this.userId = this.route.snapshot.queryParamMap.get('wikiId');
    this.getUserById(this.userId ?? "");
  }

  getUserById(id: string): void {
    this.userService.getUserById(id)
      .subscribe(user => {
        this.user = user;
        console.log('User loaded: ' + this.user);
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

  handleEditInterestsClick () {
    this.router.navigate(['/edit-interests']);
  }

}
