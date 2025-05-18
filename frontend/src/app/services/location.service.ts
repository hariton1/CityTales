import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';

@Injectable({ providedIn: 'root' })
export class LocationService {

  private LOCATION_PATH = 'api/historicPlace/'

  constructor(private httpClient: HttpClient) {
  }

  public getLocationsInRadius(latitude: number, longitude: number, radius: number): Observable<HistoricalPlaceEntity[]> {
    var params = new HttpParams().set('latitude', latitude).set('longitude', longitude).set('radius', radius);
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'by/location', {
      params: params
    });
  }

  public getLocationByVHWId(id: number): Observable<HistoricalPlaceEntity> {
    return this.httpClient.get<HistoricalPlaceEntity>(BACKEND_ADDRESS + this.LOCATION_PATH + 'by/id/' + id.toString());
  }

  public getLocationByPartialName(name: string): Observable<HistoricalPlaceEntity[]> {
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'by/name/' + name);
  }
}
