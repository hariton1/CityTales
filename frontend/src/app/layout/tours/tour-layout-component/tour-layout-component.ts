import {Component, ViewChild} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {TuiButton, TuiTextfield} from '@taiga-ui/core';
import {TuiInputModule} from '@taiga-ui/legacy';
import {CommonModule} from '@angular/common';
import {LocationService} from '../../../services/location.service';
import {TourService} from '../../../services/tour.service';
import {supabase} from '../../../user-management/supabase.service';
import {GoogleMap} from '@angular/google-maps';
import {BuildingEntity} from '../../../dto/db_entity/BuildingEntity';

@Component({
  selector: 'app-tour-layout-component',
  imports: [TuiButton,
    TuiInputModule, ReactiveFormsModule, CommonModule, GoogleMap, TuiTextfield],
  templateUrl: './tour-layout-component.html',
  standalone: true,
  styleUrl: './tour-layout-component.scss'
})
export class TourLayoutComponent {

  private locationService: LocationService;
  private tourService: TourService;

  constructor(locationService: LocationService, tourService: TourService) {
    this.locationService = locationService;
    this.tourService = tourService;
  }



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
    var userId: string | undefined = "";
    this.getUser().then(user => {
      userId = user.data.session?.access_token
      console.log(user.data.session);})

    //create tour in backend

    if(!this.startMarker || !this.endMarker){
      alert("Please select start and end point!");
      return;
    }

    this.tourService.createTour(
      this.tourName.getRawValue()!,
      this.tourDescription.getRawValue()!,
      this.startMarker?.getPosition()?.lat()!,
      this.startMarker?.getPosition()?.lng()!,
      this.endMarker?.getPosition()?.lat()!,
      this.endMarker?.getPosition()?.lng()!,
      this.selectedBuildings,
      userId!
    ).subscribe(tour => {
      this.tourDistance = (tour.distance / 1000).toFixed(2);
      this.tourDuration = (tour.durationEstimate /3600).toFixed(2);
    }
    );


    }

  async getUser(){
    return await supabase.auth.getSession();
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

  }

  addBuildingToRoute(building: any) {
      this.selectedBuildings.push(building);
      console.log(building.latitude);
    console.log(building.longitude);
      this.updateEstimate()
  }

  clearRoute() {
    this.selectedBuildings = [];
    if (this.startMarker) this.startMarker.setMap(null);
    if (this.endMarker) this.endMarker.setMap(null);
    this.startMarker = null;
    this.endMarker = null;
    this.updateEstimate()
  }

  deleteStopFromTour(building: any){
    const index = this.selectedBuildings.indexOf(building);
    if(index > -1){
      this.selectedBuildings.splice(index, 1);
    }
    this.updateEstimate()
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
}
