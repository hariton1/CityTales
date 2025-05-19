import {JsonPipe} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {TuiFilter} from '@taiga-ui/kit';
import {UserService} from '../../services/user.service';
import {InterestDto} from '../../dto/interest.dto';
import {forkJoin, of, switchMap} from 'rxjs';
import {UserInterestDto} from '../../dto/user-interest.dto';


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
