import {NgIf} from '@angular/common';
import {Component, inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {TuiFilter} from '@taiga-ui/kit';
import {InterestDto} from '../../../user_db.dto/interest.dto';
import {InterestsService} from '../../../user_db.services/interests.service';
import {forkJoin, Observable} from 'rxjs';
import {UserInterestDto} from '../../../user_db.dto/user-interest.dto';
import {UserInterestsService} from '../../../user_db.services/user-interests.service';
import {TuiAppBarBack, TuiAppBarComponent, TuiHeader} from '@taiga-ui/layout';
import {TuiAlertService, TuiButton, TuiIcon, TuiTitle} from '@taiga-ui/core';
import {TuiPlatform} from '@taiga-ui/cdk';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';


@Component({
  selector: 'app-onboarding',
  imports: [
    ReactiveFormsModule,
    ReactiveFormsModule,
    TuiFilter,
    TuiAppBarBack,
    TuiAppBarComponent,
    TuiButton,
    TuiPlatform,
    TuiHeader,
    TuiTitle,
    TuiIcon,
    NgIf
  ],
  templateUrl: './onboarding-login.component.html',
  styleUrl: './onboarding-login.component.less',
})

export class OnboardingLoginComponent implements OnInit{

  protected allInterestsList: InterestDto[] = [];
  protected interestNames: string[] = [];

  protected userInterestsList: UserInterestDto[] = [];
  protected userInterestNames: string[] = [];

  protected loading = true;
  protected error: any = null;

  isMobile = false;

  private readonly alerts = inject(TuiAlertService);


  constructor(private interestsService: InterestsService,
              private userInterestService: UserInterestsService,
              private breakpointObserver: BreakpointObserver) {
  }

  ngOnInit(): void {
    this.breakpointObserver
      .observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape])
      .subscribe(result => {
        this.isMobile = result.matches;
      });

    this.getAllInterests();
  }

  getAllInterests(): void {
    this.interestsService.getAllInterests()
      .subscribe({
        next: (interests) => {
          this.allInterestsList = interests;
          this.interestNames = interests.map(interest => interest.getInterestName());
          console.log('Interest names:', this.interestNames);
        },
        error: (error) => {
          console.error('Error loading interests:', error);
        }
      });
  }

  createInterests(): void {
    const selectInterestsNames = this.form.value.filters as string[] || [];
    let selectedInterests = this.allInterestsList.filter(interest =>
      selectInterestsNames.includes(interest.getInterestName()));

    console.log('Selected interests:', selectedInterests);
    console.log('Current user interests:', this.userInterestsList);

    // Use forkJoin to handle multiple requests and wait for all to complete
    const createRequests: Observable<any>[] = [];

    for (let interest of selectedInterests) {
      // Check if this interest is already in user's list
      const alreadyExists = this.userInterestsList.some(
        userInterest => userInterest.getInterestId() === interest.getInterestId()
      );

      if (!alreadyExists) {
        console.log('Creating interest:', interest.getInterestName(), interest.getInterestId());
        const newInterest = new UserInterestDto(
          'f5599c8c-166b-495c-accc-65addfaa572b',
          interest.getInterestId(),
          new Date(),
          1
        );
        createRequests.push(this.userInterestService.createNewUserInterest(newInterest));
      } else {
        console.log('Interest already exists:', interest.getInterestName());
      }
    }

    // Execute all requests and refresh once when all are done
    if (createRequests.length > 0) {
      forkJoin(createRequests).subscribe({
        next: (results) => {
          console.log('All interests created successfully', results);
          this.alerts
            .open('Your new interests are saved', {label: 'Success!', appearance: 'success', autoClose: 3000})
            .subscribe();
        },
        error: (err) => {
          console.error('Error creating interests:', err);
        }
      });
    } else {
      console.log('No new interests to create');
    }
  }

  onSubmit(): void {
    console.log('Form submitted with values:', this.form.value);
    this.createInterests();
  }

  protected readonly form = new FormGroup({
    filters: new FormControl([]),
  });
}
