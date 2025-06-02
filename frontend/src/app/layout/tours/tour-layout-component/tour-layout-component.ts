import {Component, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {TuiButton, TuiTextfield} from '@taiga-ui/core';
import {TuiInputModule} from '@taiga-ui/legacy';
import {CommonModule} from '@angular/common';
import {LocationService} from '../../../services/location.service';
import {TourService} from '../../../services/tour.service';
import {supabase} from '../../../user-management/supabase.service';
import {GoogleMap, MapPolyline} from '@angular/google-maps';
import {BuildingEntity} from '../../../dto/db_entity/BuildingEntity';
import {TourDto} from '../../../dto/tour.dto';
@Component({
  selector: 'app-tour-layout-component',
  imports: [TuiButton,
    TuiInputModule, ReactiveFormsModule, CommonModule, GoogleMap, TuiTextfield, MapPolyline],
  templateUrl: './tour-layout-component.html',
  standalone: true,
  styleUrl: './tour-layout-component.scss'
})
export class TourLayoutComponent {

  private locationService: LocationService;
  private tourService: TourService;
  private router: Router;
  private userId: string | null = null;

  constructor(locationService: LocationService, tourService: TourService, router: Router) {
    this.locationService = locationService;
    this.tourService = tourService;
    this.router = router;
    this.getUserId().then(userId => {
      this.userId = userId;
      console.log('async user id ' + userId);
      this.tourService.getToursForUserId(userId!).subscribe(tours => {
        this.userTours = tours.map(tour => TourDto.fromTourEntity(tour))
        console.log("User tours length: " + this.userTours.length);
        this.userTours.forEach(tour => {console.log(tour.getId())})
      })
    })
  }

  async getUserId(): Promise<string | null> {
    const { data } = await supabase.auth.getSession();
    const userId = data.session?.user?.id ?? null;
    return userId;
  }



  userTours: TourDto[] = [];


  recommendedTours = [
    { name: 'Vienna Highlights', duration: '2 hours', description: 'Visit iconic landmarks in central Vienna.' },
    { name: 'Hidden Gems Walk', duration: '3 hours', description: 'Explore less-known but charming spots.' },
    { name: 'Imperial Architecture', duration: '1.5 hours', description: 'Admire the grandeur of Vienna’s palaces.' },
  ];

  tourName = new FormControl('');
  tourDescription = new FormControl('');
  tourDuration: string = "0";
  tourDistance: string = "0";


  createTour(){
    //create tour in backend

    if(!this.startMarker || !this.endMarker){
      alert("Please select start and end point!");
      return;
    }

    var tour: TourDto = new TourDto(
      1,
      this.tourName.getRawValue()!,
      this.tourDescription.getRawValue()!,
      this.startMarker?.getPosition()?.lat()!,
      this.startMarker?.getPosition()?.lng()!,
      this.endMarker?.getPosition()?.lat()!,
      this.endMarker?.getPosition()?.lng()!,
      this.selectedBuildings,
      +this.tourDistance,
      +this.tourDuration,
      this.userId ?? 'NONE');


      this.tourDistance = (tour.getDistance() / 1000).toFixed(2);
      this.tourDuration = (tour.getDurationEstimate() /3600).toFixed(2);
      if(tour.getUserId() !== 'NONE') {
        console.log(tour.getUserId())
        this.tourService.createTourInDB(tour).subscribe({
          next: tour => {
            console.log("Tour created successfully!");},
          error: any => {console.log("An error occured when saving tour to the DB! ")}});
        this.userTours.push(tour);
        this.resetInterface();
      } else {
        alert("Tour could not be created! No user logged in. Please log in to create a tour.");
      }
    }

  //Map logic

  @ViewChild(GoogleMap, { static: false }) map!: GoogleMap;

  zoom = 16;
  center: google.maps.LatLngLiteral = { lat: 48.19994406631644, lng: 16.371089994357767 };
  mode: 'start' | 'end' | null = null;

  startMarker: google.maps.Marker | null = null;
  endMarker: google.maps.Marker | null = null;

  selectedBuildings: BuildingEntity[] = [];

  buildingData: BuildingEntity[] = [];

  markers: google.maps.Marker[] = [];

  polylinePath: google.maps.LatLngLiteral[] = [];
  polylineOptions: google.maps.PolylineOptions = {
    strokeColor: '#000000',
    strokeOpacity: 1.0,
    strokeWeight: 3,
    clickable: false
  };

  ngAfterViewInit() {
    if (!this.map?.googleMap) {
      console.error('Google Map is not initialized yet');
      return;
    }

    //TODO: replace with user location
    this.locationService.getLocationsInRadius(48.19994406631644, 16.371089994357767, 500).subscribe(locations => {
      this.buildingData = locations;
      console.log(this.buildingData.length);

      const mapRef = this.map.googleMap!;
      this.buildingData.forEach((b) => {
        const marker = new google.maps.Marker({
          position: {"lat": b.latitude, "lng": b.longitude},
          map: mapRef,
          title: b.name
        });

        marker.addListener('click', () => this.addBuildingToRoute(b));
        this.markers.push(marker);

      });
    })
  }

  onTourCardClick(tour: TourDto):void{
    this.router.navigateByUrl("/tours/" + tour.getId());
  }

  onMapClick(event: google.maps.MapMouseEvent) {
    if (!event.latLng) return;

    const mapRef = this.map.googleMap!;
    const position = event.latLng.toJSON();

    if (this.mode === 'start') {
      if (this.startMarker) this.startMarker.setMap(null);
      this.startMarker = new google.maps.Marker({
        position,
        map: mapRef,
        icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
        title: 'Start Point'
      });
      this.mode = null;
    } else if (this.mode === 'end') {
      if (this.endMarker) this.endMarker.setMap(null);
      this.endMarker = new google.maps.Marker({
        position,
        map: mapRef,
        icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
        title: 'End Point'
      });
      this.mode = null;
    }

    this.updateEstimate()
    this.updatePolylinePath();

  }

  addBuildingToRoute(building: any) {
      this.selectedBuildings.push(building);
      this.updateEstimate()
    this.updatePolylinePath()
  }

  clearRoute() {
    this.selectedBuildings = [];
    if (this.startMarker) this.startMarker.setMap(null);
    if (this.endMarker) this.endMarker.setMap(null);
    this.startMarker = null;
    this.endMarker = null;
    this.updateEstimate()
    this.updatePolylinePath()
  }

  deleteStopFromTour(building: any){
    const index = this.selectedBuildings.indexOf(building);
    if(index > -1){
      this.selectedBuildings.splice(index, 1);
    }
    this.updateEstimate()
    this.updatePolylinePath()
  }

  deleteTour(tour: TourDto): void{
    console.log(tour.getId())
    this.tourService.deleteTourById(tour.getId());
    this.userTours = this.userTours.filter(t => t.getId() !== tour.getId());
    alert("Tour deleted successfully!");
  }



  updateEstimate(){
    var availablePoints = 0;
    if(this.startMarker) {
      availablePoints++;
    }
    if(this.endMarker) {
      availablePoints++;
    }
    if(this.selectedBuildings.length > 0){
      availablePoints += this.selectedBuildings.length;
    }

    console.log(availablePoints);

    if(availablePoints < 2){
      this.tourDuration = "0";
      this.tourDistance = "0";
      return
    }


    this.tourService.getDurationDistanceEstimate(
      this.startMarker?.getPosition()?.lat()!,
      this.startMarker?.getPosition()?.lng()!,
      this.endMarker?.getPosition()?.lat()!,
      this.endMarker?.getPosition()?.lng()!,
      this.selectedBuildings
    ).subscribe(estimate => {
      this.tourDuration = (estimate.duration/1000).toFixed(2);
      this.tourDistance = (estimate.distance/3600).toFixed(2);
    })
  }

  updatePolylinePath(): void {
    var coords: google.maps.LatLngLiteral[] = [];
    if(this.startMarker) {
      coords.unshift({lat: this.startMarker.getPosition()?.lat()!, lng: this.startMarker.getPosition()?.lng()!});
    }
    this.selectedBuildings.forEach(building => {
      coords.push({lat: building.latitude, lng: building.longitude});
    })

    if(this.endMarker) {
      coords.push({lat: this.endMarker.getPosition()?.lat()!, lng: this.endMarker.getPosition()?.lng()!});
    }

    this.polylinePath = coords;
    console.log("Updated polyline path!")
    console.log(this.polylinePath.toString());
  }

  resetInterface(): void {
    this.selectedBuildings = []
    this.startMarker = null;
    this.endMarker = null;
    this.polylinePath = [];
    this.tourName.reset()
    this.tourDescription.reset();
    this.tourDuration = "0";
    this.tourDistance = "0";
    this.updatePolylinePath()
  }
}
