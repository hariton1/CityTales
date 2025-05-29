import { Component } from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import { FormControl} from '@angular/forms';
import {TuiButton} from '@taiga-ui/core';
import {TuiInputModule} from '@taiga-ui/legacy';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-tour-layout-component',
  imports: [TuiButton,
    TuiInputModule, ReactiveFormsModule, CommonModule],
  templateUrl: './tour-layout-component.html',
  standalone: true,
  styleUrl: './tour-layout-component.scss'
})
export class TourLayoutComponent {
  recommendedTours = [
    { name: 'Vienna Highlights', duration: '2 hours', description: 'Visit iconic landmarks in central Vienna.' },
    { name: 'Hidden Gems Walk', duration: '3 hours', description: 'Explore less-known but charming spots.' },
    { name: 'Imperial Architecture', duration: '1.5 hours', description: 'Admire the grandeur of Vienna’s palaces.' },
  ];

  tourName = new FormControl('');
  tourDescription = new FormControl('');

  createTour(){
    console.log("Invoked create Tour Button!")
  }




}
