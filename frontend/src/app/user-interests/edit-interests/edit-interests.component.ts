import {Component, OnInit} from '@angular/core';
import {JsonPipe} from "@angular/common";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {TuiFilter} from "@taiga-ui/kit";
import {UserInterestDto} from '../../user_db.dto/user-interest.dto'
import {UserService} from '../../services/user.service';
import {forkJoin, Observable, of, switchMap} from 'rxjs';
import {InterestDto} from '../../dto/interest.dto';
import {TuiButton, TuiIcon} from '@taiga-ui/core';
import {UserInterestsService} from '../../user_db.services/user-interests.service';

@Component({
  selector: 'app-edit-interests',
  imports: [
    JsonPipe,
    ReactiveFormsModule,
    TuiFilter,
    TuiButton,
    TuiIcon
  ],
  templateUrl: './edit-interests.component.html',
  styleUrl: './edit-interests.component.scss'
})
export class EditInterestsComponent implements OnInit{
  protected allInterestsList: InterestDto[] = [];
  protected interestNames: string[] = [];

  protected userInterestsList: UserInterestDto[] = [];
  protected userInterestNames: string[] = [];

  protected loading = true;
  protected error: any = null;

  constructor(private userService: UserService,
              private userInterestService: UserInterestsService) {

  }

  ngOnInit(): void {
    this.getAllInterests();
    this.fetchUserInterests();
  }

  fetchUserInterests(): void {
    this.loading = true;
    // Use switchMap to handle the sequential flow
    this.userService.readInterests('f5599c8c-166b-495c-accc-65addfaa572b')
      .pipe(
        switchMap(interests => {
          this.userInterestsList = interests;

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

  deleteInterest(): void {
    for (let interest of this.userInterestsList) {
      this.userService.readInterestDetail(interest.getInterestId()).subscribe(detail => {
          const selectedFilters = this.form.value.filters || [];
          if (!selectedFilters.includes(detail.getInterestName())) {
            interest.setUserId('f5599c8c-166b-495c-accc-65addfaa572b');
            console.log('Deleting interest:', interest);
            this.userInterestService.deleteUserInterest(interest).subscribe({
              next: () => {
                console.log('Interest deleted successfully');
                this.fetchUserInterests();
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
  }

  protected form = new FormGroup({
    filters: new FormControl([] as string[]),
  });

}
