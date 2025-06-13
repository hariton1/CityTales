import {ChangeDetectionStrategy, Component, inject, ViewChild, ChangeDetectorRef} from '@angular/core';
import {Router} from '@angular/router';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {TuiAlertService, TuiButton, TuiTextfield} from '@taiga-ui/core';
import {TuiInputModule} from '@taiga-ui/legacy';
import {CommonModule} from '@angular/common';
import {LocationService} from '../../../services/location.service';
import {TourService} from '../../../services/tour.service';
import {supabase} from '../../../user-management/supabase.service';
import {GoogleMap, MapPolyline} from '@angular/google-maps';
import {BuildingEntity} from '../../../dto/db_entity/BuildingEntity';
import {TourDto} from '../../../dto/tour.dto';
import {TourRequestEntity} from '../../../dto/tour_entity/TourRequestEntity';
import {FormsModule} from '@angular/forms';
import {TuiInputNumber, TuiSlider, TuiTabs} from '@taiga-ui/kit';


@Component({
  selector: 'app-tour-layout-component',
  imports: [TuiButton,
    TuiInputModule, ReactiveFormsModule, CommonModule, GoogleMap, TuiTextfield, MapPolyline, FormsModule, TuiSlider, TuiTabs, TuiInputNumber],
  templateUrl: './tour-layout-component.html',
  standalone: true,
  styleUrl: './tour-layout-component.scss',
  changeDetection: ChangeDetectionStrategy.Default
})
export class TourLayoutComponent {

  private locationService: LocationService;
  private tourService: TourService;
  private router: Router;
  private userId: string | null = null;
  private cdr: ChangeDetectorRef;
  private interestFiltering: string | null = null;
  private readonly alerts = inject(TuiAlertService);

  constructor(locationService: LocationService, tourService: TourService, router: Router, cdr: ChangeDetectorRef) {
    this.locationService = locationService;
    this.tourService = tourService;
    this.router = router;
    this.cdr = cdr;
  }

  ngOnChanges(): void {
    this.cdr.detectChanges();
  }

  async ngOnInit() {
    console.log('ngOnViewInit');
    this.interestFiltering = localStorage.getItem("interest_filtering");
    const { data } = await supabase.auth.getSession();
    const userId = data.session?.user?.id;
    console.log('async user id ' + userId);
    if (userId) {
      this.userId = userId;
      for(var id of this.recommendedToursIds){
        this.tourService.getTourForTourId(id).subscribe(tour => {
          this.recommendedTours.push(TourDto.fromTourEntity(tour));
          console.log("Fetched recommended tour: " + tour);
        })
      }

      this.tourService.getToursForUserId(userId!).subscribe(tours => {
        this.userTours = [...tours.map(tour => TourDto.fromTourEntity(tour))];
        console.log("User tours length: " + this.userTours.length);
        this.cdr.detectChanges();
      }) // Proceed to load tours for a valid user
    } else {
      console.error("User ID is null or undefined");
      // Handle the error accordingly (e.g., show an alert, redirect to login)
    }

  }


  userTours: TourDto[] | null = null;


  recommendedToursIds: number[] = [153]
  recommendedTours: TourDto[] = []

  tourName = new FormControl('');
  tourDescription = new FormControl('');
  tourDuration: string = "0";
  tourDistance: string = "0";
  protected noAdults: number | null = 0;
  noChildren = 0;
  noSeniors = 0;

  selectedTab = 0;

  onTabClick(tab: number) {
    this.selectedTab = tab;
  }


  createTour(){
    //create tour in backend

    if(!this.startMarker || !this.endMarker){
      this.alerts.open('Please select a start and end point!', {label: 'Failure!', appearance: 'warning', autoClose: 3000}).subscribe();
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
      this.userId ?? 'NONE',
      0); //TODO: Add price estimation for user defined tours


      this.tourDistance = (tour.getDistance() / 1000).toFixed(2);
      this.tourDuration = (tour.getDurationEstimate() /3600).toFixed(2);
      if(tour.getUserId() !== 'NONE') {
        this.tourService.createTourInDB(tour).subscribe({
          next: tour => {
            this.alerts.open('Your tour was created successfully!', {
              label: 'Success!',
              appearance: 'positive',
              autoClose: 3000
            }).subscribe()},
          error:any => {this.alerts.open('Your tour could not be created!', {
            label: 'Failure!',
            appearance: 'warning',
            autoClose: 3000
          }).subscribe()}});
        this.userTours!.push(tour);
        this.resetInterface();
      } else {
        this.alerts.open('No user logged in! Cannot create tour.', {
          label: 'Failure!',
          appearance: 'warning',
          autoClose: 6000
        }).subscribe();
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

  options: google.maps.MapOptions = {
    zoomControl: true,
    mapTypeControl: false,
    scaleControl: false,
    streetViewControl: false,
    rotateControl: false,
    fullscreenControl: false,
    clickableIcons: true,
    disableDoubleClickZoom: true,
    mapTypeId: 'roadmap',
    styles: [
      {
        "featureType": "poi",
        "elementType": "labels.text",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      },
      {
        "featureType": "poi.business",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      },
      {
        "featureType": "road",
        "elementType": "labels.icon",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      },
      {
        "featureType": "transit",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      }
    ]
  }

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
    console.log()
    this.locationService.getLocationsInRadius(48.19994406631644, 16.371089994357767, 1000, this.interestFiltering === 'true').subscribe(locations => {
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
    this.cdr.detectChanges();
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
    this.userTours = this.userTours!.filter(t => t.getId() !== tour.getId());
    this.alerts.open('Your tour is deleted!', {label: 'Success!', appearance: 'success', autoClose: 3000}).subscribe();
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
      this.tourDuration = (estimate.duration).toFixed(2);
      this.tourDistance = (estimate.distance).toFixed(2);
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

  minDistance = 0; // km
  maxDistance = 10.0; // km
  numberOfSites = 2;
  maxBudget = 10;

  generatedTours: TourDto[] = [];

  generateAdvancedTour() {

    //if(!this.startMarker || !this.endMarker){
    //  this.alerts.open('Please select a start and end point!', {label: 'Failure!', appearance: 'warning', autoClose: 3000}).subscribe();
    //  return;
    //}
    console.log("Nearby stops length: " + this.buildingData.length)
    console.log("Generating tour for user " + this.userId)
    var user = this.userId!;
    var entity: TourRequestEntity = {
      userId: user,
      start_lat: this.startMarker?.getPosition()?.lat()!,
      start_lng: this.startMarker?.getPosition()?.lng()!,
      end_lat: this.endMarker?.getPosition()?.lat()!,
      end_lng: this.endMarker?.getPosition()?.lng()!,
      predefinedStops: [],
      maxDistance: this.maxDistance * 1000,
      minDistance: this.minDistance * 1000,
      maxDuration: 0,
      minDuration: 0,
      maxBudget: this.maxBudget,
      numStops: this.numberOfSites,
      personConfiguration: [this.noAdults!, this.noChildren, this.noSeniors] //multiset for adults, children, seniors
    }
    this.alerts.open('Your tour is being generated...', {label: 'Success!', appearance: 'success', autoClose: 6000}).subscribe();
    this.tourService.createTour(entity).subscribe(data => {
      if(data.length === 0) {
        this.alerts.open('No tour found for your parameters! Please choose other parameters.', {label: 'Failure!', appearance: 'warning', autoClose: 6000}).subscribe();
        return;
      } else {
        this.alerts.open('Your tour finished generating!', {label: 'Success!', appearance: 'success', autoClose: 6000}).subscribe();
      }
      var tourdtos: TourDto[] = [];
      data.forEach(tour => {
        tourdtos.push(TourDto.fromTourEntity(tour))});

      tourdtos.sort((dto1, dto2) => {
        if(dto1.getDistance() < dto2.getDistance()){
          return -1;
        }
        if(dto1.getDistance() > dto2.getDistance()){
          return 1;
        }
        return 0;
      });

      tourdtos.forEach(tour => {console.log(tour.getId(), tour.getDistance())})

      var selectedTour: TourDto = tourdtos[0];

      this.tourService.createTourInDB(selectedTour).subscribe({
        next: tour => {console.log("Tour created successfully!");
          this.tourService.getToursForUserId(this.userId!).subscribe(tours => {
            this.userTours = tours.map(tour => TourDto.fromTourEntity(tour));
          })
          },
        error: any => {console.log("An error occured when saving tour to the DB! ")}
      });
    })
  }

  onGeneratedTourClick(tour: TourDto) {
    this.router.navigateByUrl("/tours/" + tour.getId());
  }


}
