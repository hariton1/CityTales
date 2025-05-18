import {Component, Output} from '@angular/core';
import { GoogleMapsModule} from '@angular/google-maps';
import { CommonModule} from '@angular/common';
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {UserLocationService} from '../../services/user-location.service';
import {LocationService} from '../../services/location.service';
import {EventEmitter} from '@angular/core';
@Component({
  selector: 'app-map-view',
  imports: [
    GoogleMapsModule,
    CommonModule
  ],
  templateUrl: './map-view.component.html',
  styleUrl: './map-view.component.scss'
})
export class MapViewComponent {

  constructor(private locationService: LocationService,
              private userLocationService: UserLocationService) {
  }

  @Output() selectPlaceEvent: EventEmitter<HistoricalPlaceEntity> = new EventEmitter<HistoricalPlaceEntity>();

  locationsNearby: HistoricalPlaceEntity[] = [];

  markers: any[] = [];
  center: google.maps.LatLngLiteral = { lat: 48.19865798950195, lng: 16.3714542388916 };
  zoom = 15;

  options: google.maps.MapOptions = {
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    maxZoom: 20,
    minZoom: 4,
    zoomControl: true,
    clickableIcons: true,

  };

  markerOptions: google.maps.MarkerOptions = {
    draggable: false,
    animation: google.maps.Animation.DROP
  }

  locationMarkerOptions: google.maps.MarkerOptions = {
    draggable: false,
    animation: google.maps.Animation.BOUNCE,
    icon: {
      url: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png',
      scaledSize: new google.maps.Size(25, 25),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(17, 34),
      labelOrigin: new google.maps.Point(12, 34)
    }
  }

  ngOnInit(): void {
    var location = this.userLocationService.getPosition();

    location.then(position => {
      this.center = {lat: position.lat, lng: position.lng};
      this.locationService.getLocationsInRadius(position.lat, position.lng, 3000).subscribe(locations => {
        this.locationsNearby = locations;
        this.addMarkersToMap(locations);
      })
    })
  }

  addMarkersToMap(locations: HistoricalPlaceEntity[]): void {
    locations.forEach(location => {
      this.markers.push({lat: location.building.latitude, lng: location.building.longitude});
    })
  }

  openMarkerInfo(location: HistoricalPlaceEntity): void {
    this.selectPlaceEvent.emit(location);
  }
}
