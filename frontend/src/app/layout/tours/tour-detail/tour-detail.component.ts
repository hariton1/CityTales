import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {TourDto} from '../../../dto/tour.dto';
import {TourService} from '../../../services/tour.service';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {TuiAlertService, TuiTextfield} from '@taiga-ui/core';
import {GoogleMap, MapMarker, MapPolyline} from '@angular/google-maps';
import {CommonModule} from '@angular/common';
import {BuildingEntity} from '../../../dto/db_entity/BuildingEntity';


@Component({
  selector: 'app-tour-detail',
  imports: [TuiTextfield, ReactiveFormsModule, GoogleMap, MapMarker, CommonModule, MapPolyline],
  templateUrl: './tour-detail.component.html',
  styleUrl: './tour-detail.component.scss'
})
export class TourDetailComponent {

  tourId: number = 0;
  tour: TourDto = new TourDto(0, "", "", 0, 0, 0, 0, [], 0, 0, "");
  private tourService: TourService;
  private router: Router;
  private readonly alerts = inject(TuiAlertService);




  constructor(private route: ActivatedRoute, tourService: TourService, router: Router) {
    this.route.params.subscribe(params => {this.tourId = params['id'];})
    this.tourService = tourService;
    this.router = router;
    console.log(this.tourId);
    tourService.getTourForTourId(this.tourId).subscribe(tour => {
      this.tour = TourDto.fromTourEntity(tour);
      this.nameControl.setValue(this.tour.getName());
      this.descriptionControl.setValue(this.tour.getDescription());
      this.start = {lat: this.tour.getStart_lat(), lng: this.tour.getStart_lng()};
      this.end = {lat: this.tour.getEnd_lat(), lng: this.tour.getEnd_lng()};
      this.polylinePath.push(this.start);
      this.tour.getStops().forEach(stop => {
        this.polylinePath.push({lat: stop.latitude, lng: stop.longitude});
      })
      this.polylinePath.push(this.end);
      console.log(this.polylinePath.toString());
    })
  }

  nameControl = new FormControl('');
  descriptionControl = new FormControl('');

  saveChanges() {
    this.tour.setName(this.nameControl.value!);
    this.tour.setDescription(this.descriptionControl.value!);
    this.tourService.updateTour(this.tour).subscribe({
      next: tour => {
        console.log("Tour updated successfully!");
        this.alerts
          .open('Your tour is updated!', {label: 'Success!', appearance: 'success', autoClose: 3000})
          .subscribe();},
      error: any => {console.log("An error occurred when updating tour! ")
        this.alerts
          .open('Your tour could not be updated.', {label: 'Failure!', appearance: 'warning', autoClose: 3000})
          .subscribe();}}
    );
  }

  deleteStop(stop: BuildingEntity) {
    this.tour.getStops().splice(this.tour.getStops().indexOf(stop), 1);
    this.updateEstimate();
  }

  deleteTour() {
    this.tourService.deleteTourById(this.tourId);
    this.router.navigateByUrl("/tours");
  }

  getMapCenter() {
    const stops = this.tour.getStops();
    if (stops && stops.length > 0) {
      return { lat: stops[0].latitude, lng: stops[0].longitude };
    }
    return { lat: 48.2082, lng: 16.3738 }; // fallback Vienna center
  }

  updateEstimate(){
    this.tourService.getDurationDistanceEstimate(
      this.start.lat,
      this.start.lng,
      this.end.lat,
      this.end.lng,
      this.tour.getStops()
    ).subscribe(estimate => {
      this.tour.setDurationEstimate(+(estimate.duration/3600).toFixed(2));
      this.tour.setDistance(+(estimate.distance/100).toFixed(2));
    })
  }



  //Map logic
  polylinePath: google.maps.LatLngLiteral[] = [];
  polylineOptions: google.maps.PolylineOptions = {
    strokeColor: '#000000',
    strokeOpacity: 1.0,
    strokeWeight: 3,
    clickable: false
  };

  start = {lat: 48.2082, lng: 16.3738};
  end = {lat: 48.2082, lng: 16.3738};
}
