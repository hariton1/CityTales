import {HttpClient} from '@angular/common/http';
import { HttpParams } from '@angular/common/http';

import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';
import {HistoricalPersonEntity} from '../dto/db_entity/HistoricalPersonEntity';
import {BuildingEntity} from '../dto/db_entity/BuildingEntity';
import {HistoricalEventEntity} from '../dto/db_entity/HistoricalEventEntity';
import {PersonEntity} from '../dto/db_entity/PersonEntity';
import {EventEntity} from '../dto/db_entity/EventEntity';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class SearchService {

  constructor(private httpClient: HttpClient) {
  }

  public searchLocation(text: string): Observable<BuildingEntity[]> {
    const params = new HttpParams().set('query', text);
    return this.httpClient.get<BuildingEntity[]>(BACKEND_ADDRESS + 'api/search/searchPlaces', {
      params: params
    });
  }
  public getBuildingById(id: string): Observable<BuildingEntity> {
    const url = BACKEND_ADDRESS + `api/historicPlace/by/id/${id}`;
    console.log('[GET] getBuildingById →', url);
    return this.httpClient.get<BuildingEntity>(url).pipe(
      tap(response => console.log('[RESPONSE] getBuildingById:', response))
    );
  }

  public searchPersons(text: string): Observable<HistoricalPersonEntity[]> {
    const params = new HttpParams().set('query', text);
    return this.httpClient.get<HistoricalPersonEntity[]>(BACKEND_ADDRESS + 'api/search/searchPersons', {
      params: params
    });
  }

  public getPersonById(id: string): Observable<PersonEntity> {
    const url = BACKEND_ADDRESS + `api/historicPerson/by/id/${id}`;
    console.log('[GET] getPersonById →', url);
    return this.httpClient.get<PersonEntity>(url).pipe(
      tap(response => console.log('[RESPONSE] getPersonById:', response))
    );
  }

  public searchEvents(text: string): Observable<HistoricalEventEntity[]> {
    const params = new HttpParams().set('query', text);
    return this.httpClient.get<HistoricalEventEntity[]>(BACKEND_ADDRESS + 'api/search/searchEvents', {
      params: params
    });
  }

  public getEventById(id: string): Observable<EventEntity> {
    const url = BACKEND_ADDRESS + `api/historicEvent/by/id/${id}`;
    console.log('[GET] getEventById →', url);
    return this.httpClient.get<EventEntity>(url).pipe(
      tap(response => console.log('[RESPONSE] getEventById:', response))
    );
  }

}
