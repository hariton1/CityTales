import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {TourEntity} from '../dto/tour_entity/TourEntity';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';

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
                    stops: HistoricalPlaceEntity[],
                    userId: string): Observable<TourEntity> {
    const params = new HttpParams().set('name', name)
      .set("description", description)
      .set("start_lat", start_lat.toString())
      .set("start_long", start_long.toString())
      .set("end_lat", end_lat.toString())
      .set("end_long", end_long.toString())
      .set("stops", JSON.stringify(stops))
      .set("userId", userId);
    return this.httpClient.get<TourEntity>(BACKEND_ADDRESS + 'api/tour/createTour', {
      params: params
    });
  }
}
