import {Component, inject, ViewChild} from '@angular/core';
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
import {TuiSlider} from '@taiga-ui/kit';
import * as localForage from 'localforage';
import {fromEvent, mapTo, merge, Observable, of} from 'rxjs';


@Component({
  selector: 'app-tour-layout-component',
  imports: [TuiButton,
    TuiInputModule, ReactiveFormsModule, CommonModule, GoogleMap, TuiTextfield, MapPolyline, FormsModule, TuiSlider],
  templateUrl: './tour-layout-component.html',
  standalone: true,
  styleUrl: './tour-layout-component.scss'
})
export class TourLayoutComponent {
  online$: Observable<boolean>;

  private locationService: LocationService;
  private tourService: TourService;
  private router: Router;
  private userId: string | null = null;
  private readonly alerts = inject(TuiAlertService);

  constructor(locationService: LocationService, tourService: TourService, router: Router) {
    this.online$ = merge(
      of(navigator.onLine),
      fromEvent(window, 'online').pipe(mapTo(true)),
      fromEvent(window, 'offline').pipe(mapTo(false))
    );

    this.locationService = locationService;
    this.tourService = tourService;
    this.router = router;

    this.getUserId().then(async userId => {
      this.userId = userId;
      if (!userId) return;

      const cacheKey = `userTours_${userId}`;
      const isOnline = navigator.onLine;

      if (!isOnline) {
        const cachedTours = await localForage.getItem<any[]>(cacheKey);
        if (cachedTours && cachedTours.length > 0) {
          console.log('Loaded tours from cache');

          this.userTours = cachedTours.map(t => {
            // Parse stops JSON string to array
            const parsedStops = JSON.parse(t.stops || '[]');
            // Map each stop to only lat/lng
            const simplifiedStops = parsedStops.map((stop: any) => ({
              latitude: stop.latitude,
              longitude: stop.longitude
            }));

            return new TourDto(
              t.id,
              t.name,
              t.description,
              t.start_lat,
              t.start_lng,
              t.end_lat,
              t.end_lng,
              simplifiedStops,
              t.distance,
              t.durationEstimate,
              t.userId
            );
          });
          console.log(this.userTours)
        } else {
          this.alerts.open('Offline and no tour cache found.', {
            label: 'Offline',
            appearance: 'warning',
            autoClose: 5000
          }).subscribe();
        }
      } else {
        this.tourService.getToursForUserId(userId).subscribe(tours => {
          this.userTours = tours.map(tour => TourDto.fromTourEntity(tour));
          console.log('Fetched tours from backend');
          // Serialize before caching
          const serializable = this.userTours.map(t => TourDto.ofTourDTo(t));
          localForage.setItem(cacheKey, serializable);
        });
      }
    });
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
      this.userId ?? 'NONE');


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
        this.userTours.push(tour);
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
    this.locationService.getLocationsInRadius(48.19994406631644, 16.371089994357767, 1000, true).subscribe(locations => {
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

  minDistance = 1.0; // km
  maxDistance = 10.0; // km
  minSites = 2;
  maxBudget = 10;

  generatedTours: TourDto[] = [];

  generateAdvancedTour() {

    if(!this.startMarker || !this.endMarker){
      this.alerts.open('Please select a start and end point!', {label: 'Failure!', appearance: 'warning', autoClose: 3000}).subscribe();
      return;
    }

    console.log("Generating tour for user " + this.userId)
    var user = this.userId!;
    var entity: TourRequestEntity = {
      userId: user,
      start_lat: this.startMarker?.getPosition()?.lat()!,
      start_lng: this.startMarker?.getPosition()?.lng()!,
      end_lat: this.endMarker?.getPosition()?.lat()!,
      end_lng: this.endMarker?.getPosition()?.lng()!,
      predefined_stops: [],
      maxDistance: this.maxDistance * 1000,
      minDistance: this.minDistance * 1000,
      maxDuration: 0,
      minDuration: 0,
      maxBudget: this.maxBudget,
      minIntermediateStops: this.minSites
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


      var selectedTour: TourDto = tourdtos[tourdtos.length - 1];


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

  generateGoogleMapsTourLink(tour: TourDto) {
    console.log(tour.getStops())
    const stops = tour.getStops();

    const locations: string[] = [];

    locations.push(`${tour.getStart_lat()},${tour.getStart_lng()}`);

    for (const stop of stops) {
      locations.push(`${stop.latitude},${stop.longitude}`);
    }

    locations.push(`${tour.getEnd_lat()},${tour.getEnd_lng()}`);

    const baseUrl = 'https://www.google.com/maps/dir/';
    const url = baseUrl + locations.map(encodeURIComponent).join('/');

    console.log(url)
    window.open(url, '_blank');
  }
}
