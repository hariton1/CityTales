import {Component, inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {TuiFilter} from "@taiga-ui/kit";
import {UserInterestDto} from '../../user_db.dto/user-interest.dto'
import {forkJoin, Observable, of, switchMap} from 'rxjs';
import {InterestDto} from '../../user_db.dto/interest.dto';
import {TuiAlertService, TuiButton, TuiIcon, TuiTitle} from '@taiga-ui/core';
import {UserInterestsService} from '../../user_db.services/user-interests.service';
import {InterestsService} from '../../user_db.services/interests.service';
import {TuiAppBarBack, TuiAppBarComponent, TuiHeader} from '@taiga-ui/layout';
import {TuiPlatform} from '@taiga-ui/cdk';
import {NgIf} from '@angular/common';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'app-edit-interests',
  imports: [
    ReactiveFormsModule,
    TuiFilter,
    TuiButton,
    TuiIcon,
    TuiHeader,
    TuiTitle,
    TuiPlatform,
    TuiAppBarComponent,
    TuiAppBarBack,
    NgIf,
  ],
  templateUrl: './edit-interests.component.html',
  styleUrl: './edit-interests.component.less'
})
export class EditInterestsComponent implements OnInit{
  protected allInterestsList: InterestDto[] = [];
  protected interestNames: string[] = [];

  protected userInterestsList: UserInterestDto[] = [];
  protected userInterestNames: string[] = [];

  protected loading = true;
  protected error: any = null;

  isMobile = false;

  private readonly alerts = inject(TuiAlertService);

  constructor(
    private interestsService: InterestsService,
    private userInterestService: UserInterestsService,
    private breakpointObserver: BreakpointObserver) {}

  ngOnInit(): void {
    this.breakpointObserver
      .observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape])
      .subscribe(result => {
        this.isMobile = result.matches;
      });

    this.getAllInterests();
    this.fetchUserInterests();
  }

  fetchUserInterests(): void {
    this.loading = true;
    // Use switchMap to handle the sequential flow
    this.userInterestService.getUserInterestsByUserId('f5599c8c-166b-495c-accc-65addfaa572b')
      .pipe(
        switchMap(interests => {
          this.userInterestsList = interests;

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
          // Extract names from the interest details
          this.userInterestNames = interestDetails.map(detail => detail.getInterestName());
          console.log('Interest names:', this.userInterestNames);
          this.form.get('filters')?.setValue(this.userInterestNames);
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching interests or details:', err);
          this.error = err;
          this.loading = false;
        }
      });
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
    const selectInterestsNames = this.form.value.filters || [];
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
          this.fetchUserInterests(); // Refresh the list only once after all operations
        },
        error: (err) => {
          console.error('Error creating interests:', err);
        }
      });
    } else {
      console.log('No new interests to create');
    }
  }

  deleteInterest(): void {
    for (let interest of this.userInterestsList) {
      this.interestsService.getInterestByInterestId(interest.getInterestId()).subscribe(detail => {
          const selectedFilters = this.form.value.filters || [];
          if (!selectedFilters.includes(detail.getInterestName())) {
            interest.setUserId('f5599c8c-166b-495c-accc-65addfaa572b');
            console.log('Deleting interest:', interest);
            this.userInterestService.deleteUserInterest(interest).subscribe({
              next: () => {
                console.log('Interest deleted successfully')
                this.userInterestNames = this.form.get('filters')?.value as string[];
              },
              error: (err) => {
                console.error('Error deleting interest:', err);
              }
            });
          }
      });
    }
  }

  onSubmit(): void {
    console.log('Form submitted with values:', this.form.value);
    this.deleteInterest();
    this.createInterests();
  }

  protected form = new FormGroup({
    filters: new FormControl([] as string[]),
  });

}
