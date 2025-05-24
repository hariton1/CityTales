import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';
import {HistoricalPersonEntity} from '../dto/db_entity/HistoricalPersonEntity';
import {HistoricalEventEntity} from '../dto/db_entity/HistoricalEventEntity';
import {BuildingEntity} from '../dto/db_entity/BuildingEntity';
import {PersonEntity} from '../dto/db_entity/PersonEntity';
import {EventEntity} from '../dto/db_entity/EventEntity';

@Injectable({ providedIn: 'root' })
export class LocationService {

  private LOCATION_PATH = 'api/historicPlace/'

  constructor(private httpClient: HttpClient) {
  }

  public getLocationsInRadius(latitude: number, longitude: number, radius: number): Observable<BuildingEntity[]> {
    var params = new HttpParams().set('latitude', latitude).set('longitude', longitude).set('radius', radius);
    return this.httpClient.get<BuildingEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'by/location', {
      params: params
    });
  }

  public getLocationByVHWId(id: number): Observable<HistoricalPlaceEntity> {
    return this.httpClient.get<HistoricalPlaceEntity>(BACKEND_ADDRESS + this.LOCATION_PATH + 'by/id/' + id.toString());
  }

  public getLocationByPartialName(name: string): Observable<HistoricalPlaceEntity[]> {
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'by/name/' + name);
  }

  public getLinkedLocations(viennaHistoryWikiId: string): Observable<BuildingEntity[]> {
    return this.httpClient.get<BuildingEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'links/buildings/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedPersons(viennaHistoryWikiId: string): Observable<PersonEntity[]> {
    return this.httpClient.get<PersonEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'links/persons/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedEvents(viennaHistoryWikiId: string): Observable<EventEntity[]> {
    return this.httpClient.get<EventEntity[]>(BACKEND_ADDRESS + this.LOCATION_PATH + 'links/events/by/id/' + viennaHistoryWikiId);
  }
}
