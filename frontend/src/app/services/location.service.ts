import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';
import {HistoricalPersonEntity} from '../dto/db_entity/HistoricalPersonEntity';
import {HistoricalEventEntity} from '../dto/db_entity/HistoricalEventEntity';

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

  public getLinkedLocations(viennaHistoryWikiId: string): Observable<HistoricalPlaceEntity[]> {
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'links/buildings/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedPersons(viennaHistoryWikiId: string): Observable<HistoricalPersonEntity[]> {
    return this.httpClient.get<HistoricalPersonEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'links/persons/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedEvents(viennaHistoryWikiId: string): Observable<HistoricalEventEntity[]> {
    return this.httpClient.get<HistoricalEventEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'links/events/by/id/' + viennaHistoryWikiId);
  }


}
