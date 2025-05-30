import { Component } from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-tour-detail',
  imports: [],
  templateUrl: './tour-detail.component.html',
  styleUrl: './tour-detail.component.scss'
})
export class TourDetailComponent {

  constructor(private route: ActivatedRoute) {
    this.route.params.subscribe(params => {console.log(params)})
  }
}
