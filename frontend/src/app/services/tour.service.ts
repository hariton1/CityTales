import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {TourEntity} from '../dto/tour_entity/TourEntity';
import {BuildingEntity} from '../dto/db_entity/BuildingEntity';

@Injectable({
  providedIn: 'root'
})
export class TourService {

  constructor(private httpClient: HttpClient) {
  }

  public createTour(name: string,
                    description: string,
                    start_lat: number,
                    start_long: number,
                    end_lat: number,
                    end_long: number,
                    stops: BuildingEntity[],
                    userId: string): Observable<TourEntity> {
    const body = {
      name,
      description,
      start_lat,
      start_long,
      end_lat,
      end_long,
      stops,
      userId
    };
    return this.httpClient.post<TourEntity>(BACKEND_ADDRESS + 'api/tour/createTour', body);
  }

  public getDurationDistanceEstimate(start_lat: number,
                                     start_lng: number,
                                     end_lat: number,
                                     end_lng: number,
                                     stops: BuildingEntity[]) {
    const body = {
      start_lat,
      start_lng,
      end_lat,
      end_lng,
      stops
    };

    console.log("Request body: " + body.start_lng);
    console.log("Request body: " + body.start_lat);
    console.log("Request body: " + body.end_lat);
    console.log("Request body: " + body.end_lng);
    console.log("Request body: " + body.stops.toString());

    return this.httpClient.post<any>(BACKEND_ADDRESS + 'api/tour/durationDistanceEstimate', body);
  }

}
