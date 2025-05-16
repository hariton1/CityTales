import {JsonPipe} from '@angular/common';
import {AfterContentInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {TuiFilter} from '@taiga-ui/kit';
import {UserService} from '../../services/user.service';
import {UserInterestDto} from '../../dto/user-interest.dto';
import {forkJoin, Observable, of, switchMap} from 'rxjs';
import {InterestDto} from '../../dto/interest.dto';


@Component({
  selector: 'app-onboarding',
  imports: [
    ReactiveFormsModule,
    JsonPipe,
    ReactiveFormsModule,
    TuiFilter
  ],
  templateUrl: './onboarding.component.html',
  styleUrl: './onboarding.component.scss',
})

export class OnboardingComponent implements OnInit{

  //protected interests: Observable<UserInterestDto[]>;
  //protected interestsList: UserInterestDto[] = [];

  //protected interestDetail: Observable<InterestDto>;
  protected allInterestsList: InterestDto[] = [];
  protected interestNames: string[] = [];
  protected loading = true;
  protected error: any = null;


  constructor(private userService: UserService) {

  }

  ngOnInit(): void {
    this.getAllInterests();
  }

  /*
  fetchUserInterests(): void {
    this.loading = true;

    // Use switchMap to handle the sequential flow
    this.userService.readInterests('f5599c8c-166b-495c-accc-65addfaa572b')
      .pipe(
        switchMap(interests => {
          this.interestsList = interests;

          // If no interests, return an empty array observable
          if (interests.length === 0) {
            return of([]);
          }

          // Create an array of observables for each interest detail request
          const detailRequests = interests.map(interest =>
            this.userService.readInterestDetail(interest.getInterestId())
          );

          // Use forkJoin to wait for all detail requests to complete
          return forkJoin(detailRequests);
        })
      )
      .subscribe({
        next: (interestDetails) => {
          // Extract names from the interest details
          this.interestNames = interestDetails.map(detail => detail.getInterestName());
          console.log('Interest names:', this.interestNames);
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching interests or details:', err);
          this.error = err;
          this.loading = false;
        }
      });
  } */


  getAllInterests(): void {
    this.userService.getAllInterests()
      .subscribe({
        next: (interests) => {
          this.interestNames = interests.map(interest => interest.getInterestName());
          console.log('Interest names:', this.interestNames);
        },
        error: (error) => {
          console.error('Error loading interests:', error);
        }
      });
  }



  protected readonly form = new FormGroup({
    filters: new FormControl([]),
  });

}
