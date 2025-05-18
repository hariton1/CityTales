import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS, SERVER_ADDRESS} from '../globals';
import {LocationDto} from '../dto/location.dto';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';

@Injectable({ providedIn: 'root' })
export class LocationService {

  constructor(private httpClient: HttpClient) {
  }

  public getLocationsInRadius(latitude: number, longitude: number, radius: number): Observable<HistoricalPlaceEntity[]> {
    var params = new HttpParams().set('latitude', latitude).set('longitude', longitude).set('radius', radius);
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + 'api/historicPlace/by/location', {
      params: params
    });
  }


  public createLocation(locationDto: LocationDto): Observable<LocationDto> {
    return this.httpClient.post<LocationDto>(SERVER_ADDRESS + 'location/create', {
      params: [],
      data: locationDto
    });
  }

  public readLocation(locationId: number): Observable<LocationDto> {
    return this.httpClient.get<LocationDto>(SERVER_ADDRESS + 'location/read' + locationId);
  }

  public updateLocation(locationDto: LocationDto): Observable<LocationDto> {
    return this.httpClient.put<LocationDto>(SERVER_ADDRESS + 'location/update', {
      params: [],
      data: locationDto
    });
  }

  public deleteLocation(locationId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'location/delete/' + locationId);
  }
}
