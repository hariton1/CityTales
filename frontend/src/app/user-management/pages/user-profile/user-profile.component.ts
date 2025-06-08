import { Component } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {
  TuiButton,
  TuiIcon,
  TuiLabel,
  TuiTextfieldComponent,
  TuiTextfieldDirective,
  TuiTitle
} from '@taiga-ui/core';
import {TuiHeader} from '@taiga-ui/layout';
import {TuiDay} from '@taiga-ui/cdk';
import {UserService} from '../../../user_db.services/user.service';
import {UserDto} from '../../../user_db.dto/user.dto';
import {UUID} from 'node:crypto';
import {forkJoin, of, switchMap} from 'rxjs';
import {UserInterestsService} from '../../../user_db.services/user-interests.service';
import {InterestsService} from '../../../user_db.services/interests.service';
import {TuiLineClamp} from '@taiga-ui/kit';

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
    TuiTitle,
    TuiLineClamp
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent {

  private userId: UUID;
  private user: UserDto | null = null;
  protected accountCreated: any = null;
  protected userInterests: string = "";

  constructor(private userService: UserService,
              private userInterestService: UserInterestsService,
              private interestsService: InterestsService,
              private router: Router) {
    this.userId = localStorage.getItem("user_uuid") as UUID;
  }

  ngOnInit(): void {
    console.log('Processing profile for userId:' + localStorage.getItem("user_uuid") as UUID);
    this.getUserById(this.userId);
    this.fetchUserInterests();
  }

  protected linesLimit = 2;

  protected toggle(): void {
    this.linesLimit = this.collapsed ? 12 : 2;
  }

  private get collapsed(): boolean {
    return this.linesLimit === 2;
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

  fetchUserInterests(): void {
    if (!this.userId) return;

    // Use switchMap to handle the sequential flow
    this.userInterestService.getMyInterests()
      .pipe(
        switchMap(interests => {
          // If no interests, return an empty array observable
          if (interests.length === 0) {
            return of([]);
          }

          // Create an array of observables for each interest detail request
          const detailRequests = interests.map(interest =>
            this.interestsService.getInterestByInterestId(interest.getInterestId())
          );

          // Use forkJoin to wait for all detail requests to complete
          return forkJoin(detailRequests);
        })
      )
      .subscribe({
        next: (interestDetails) => {
          // Extract names from the interest details and join them into a single string
          const interestNames = interestDetails.map(detail => detail.getInterestName());
          this.userInterests = interestNames.join(', '); // Store all interests in a single string
          console.log('User interests:', this.userInterests);
        },
        error: (err) => {
          console.error('Error fetching interests or details:', err);
          this.userInterests = ""; // Reset to empty string on error
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

  handleEditInterestsClick () {
    this.router.navigate(['/edit-interests']);
  }

  handleChangePassClick () {
    this.router.navigate(['/reset-pass']);
  }

  handleDisplayHistoryClick () {
    this.router.navigate(['/my-history']);
  }

  get role(): string {
    return this.user?.role ?? '';
  }

  get email(): string {
    return this.user?.email ?? '';
  }

}
