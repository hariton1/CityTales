import { Injectable } from '@angular/core';
import {LocationService} from './location.service';
import {BuildingEntity} from '../dto/db_entity/BuildingEntity';

@Injectable({
  providedIn: 'root'
})
export class UserLocationService {

  constructor(readonly locationService: LocationService) { }

  getPosition(): Promise<any>
  {
    return new Promise((resolve, reject) => {

      navigator.geolocation.getCurrentPosition(resp => {

          resolve({lng: resp.coords.longitude, lat: resp.coords.latitude});
        },
        err => {
          reject(err);
        });
    });
  }

  private watchId: number | null = null;
  private lastSentAt = 0; //last request sent to backend
  private historicalPlacesNearby: BuildingEntity[] = [];

  startTracking(): void {
    const radius = 300; //default radius in meters
    if (!('geolocation' in navigator)) {
      console.warn('Geolocation not supported.');
      return;
    }
    this.watchId = navigator.geolocation.watchPosition(
      position => {
        //limit backend calls to 1 every 20s
        const now = Date.now();
        if (now - this.lastSentAt > 20000) {
          this.lastSentAt = now;
          //send location to backend
          this.locationService
            .getLocationsInRadius(position.coords.latitude, position.coords.longitude, radius)
            .subscribe(historicalPlaces => {
              console.log(historicalPlaces.length);
              this.historicalPlacesNearby = historicalPlaces;
            })
        }
      },
      error => {
        console.error('Error getting position', error);
      },
      {
        enableHighAccuracy: true,
        maximumAge: 15000, //if the position is older than 15 seconds, a new position is requested
        timeout: 10000 //if no position is received within 10 seconds, the error callback is called
      }
    );
  }

  //Stop tracking the user's location
  stopTracking(): void {
    if (this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
      this.watchId = null;
    }
  }

}
