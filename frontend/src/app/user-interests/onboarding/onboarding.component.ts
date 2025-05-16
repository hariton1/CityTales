import {JsonPipe} from '@angular/common';
import {Component} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {TuiFilter} from '@taiga-ui/kit';
import {UserService} from '../../services/user.service';


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

export class OnboardingComponent {

  constructor(private userService: UserService) {
  }


  protected readonly form = new FormGroup({
    filters: new FormControl(['Food']),
  });

  protected readonly items = [
    'News',
    'Food',
    'Clothes',
    'Popular',
    'Goods',
    'Furniture',
    'Tech',
    'Building materials',
  ];

}
