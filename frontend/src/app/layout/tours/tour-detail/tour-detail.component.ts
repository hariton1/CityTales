import { Component } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TourDto} from '../../../dto/tour.dto';
import {TourService} from '../../../services/tour.service';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {TuiTextfield} from '@taiga-ui/core';
import {GoogleMap, MapMarker} from '@angular/google-maps';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-tour-detail',
  imports: [TuiTextfield, ReactiveFormsModule, GoogleMap, MapMarker, CommonModule],
  templateUrl: './tour-detail.component.html',
  styleUrl: './tour-detail.component.scss'
})
export class TourDetailComponent {

  tourId: number = 0;
  tour: TourDto = new TourDto(0, "", "", 0, 0, 0, 0, [], 0, 0, "");
  private tourService: TourService;


  constructor(private route: ActivatedRoute, tourService: TourService) {
    this.route.params.subscribe(params => {this.tourId = params['id'];})
    this.tourService = tourService;
    tourService.getTourForTourId(this.tourId).subscribe(tour => {
      this.tour = TourDto.fromTourEntity(tour);
      console.log("Tour: " + this.tour);
    })
  }

  nameControl = new FormControl('');
  descriptionControl = new FormControl('');

  ngOnInit() {
    this.nameControl.setValue(this.tour.getName());
    this.descriptionControl.setValue(this.tour.getDescription());
  }

  saveChanges() {
    this.tour.setName(this.nameControl.value!);
    this.tour.setDescription(this.descriptionControl.value!);
    // Optionally notify parent component of change
  }

  deleteStop(stop: any) {
  }

  deleteTour() {
    // Emit event or call service to delete the tour
  }

  getMapCenter() {
    const stops = this.tour.getStops();
    if (stops && stops.length > 0) {
      return { lat: stops[0].latitude, lng: stops[0].longitude };
    }
    return { lat: 48.2082, lng: 16.3738 }; // fallback Vienna center
  }
}
