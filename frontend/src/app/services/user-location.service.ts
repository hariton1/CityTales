import { Injectable } from '@angular/core';
import {LocationService} from './location.service';
import {BuildingEntity} from '../dto/db_entity/BuildingEntity';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserLocationService {

  private _nrOfHistoricalPlaces = new BehaviorSubject<number>(0);
  readonly nrOfHistoricalPlaces$ = this._nrOfHistoricalPlaces.asObservable();

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
  private currentPosition: { lat: number, lng: number } | null = null;

  private historicalPlacesSubject = new BehaviorSubject<BuildingEntity[]>([]);
  public historicalPlaces$ = this.historicalPlacesSubject.asObservable();

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

          this.currentPosition = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
          };
          //send location to backend
          this.locationService
            .getLocationsInRadius(position.coords.latitude, position.coords.longitude, radius, true)
            .subscribe(historicalPlaces => {
              console.log(historicalPlaces.length);
              this._nrOfHistoricalPlaces.next(historicalPlaces.length);
              this.historicalPlacesSubject.next(historicalPlaces);
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

  calculateDistanceToBuilding(building: BuildingEntity): number {
    if (!this.currentPosition || building.longitude === undefined || building.latitude === undefined) {
      return 0;
    }

    const userLat = this.currentPosition.lat;
    const userLng = this.currentPosition.lng;
    const buildingLat = building.latitude;
    const buildingLng = building.longitude;

    // Calculate distance using Haversine formula
    const R = 6371;
    const dLat = this.deg2rad(buildingLat - userLat);
    const dLon = this.deg2rad(buildingLng - userLng);
    const a =
      Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(this.deg2rad(userLat)) * Math.cos(this.deg2rad(buildingLat)) *
      Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    const distanceInKm = R * c;

    //return distance in meters
    return Math.round(distanceInKm * 1000);
  }

  private deg2rad(deg: number): number {
    return deg * (Math.PI/180);
  }

  get nrOfHistoricalPlaces(): number {
    return this._nrOfHistoricalPlaces.value;
  }

  //Stop tracking the user's location
  stopTracking(): void {
    if (this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
      this.watchId = null;
    }
  }

}
