import { Component } from '@angular/core';
import {GoogleMap, MapMarker} from '@angular/google-maps';

@Component({
  selector: 'app-map-view',
  imports: [
    GoogleMap,
    MapMarker
  ],
  templateUrl: './map-view.component.html',
  styleUrl: './map-view.component.scss'
})
export class MapViewComponent {
  center: google.maps.LatLngLiteral = { lat: 48.19865798950195, lng: 16.3714542388916 };
  zoom = 12;

  markerPosition: google.maps.LatLngLiteral = this.center;
}
