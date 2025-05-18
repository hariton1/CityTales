import {HttpClient} from '@angular/common/http';
import { HttpParams } from '@angular/common/http';

import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';
import {HistoricalPersonEntity} from '../dto/db_entity/HistoricalPersonEntity';

@Injectable({ providedIn: 'root' })
export class SearchService {

  constructor(private httpClient: HttpClient) {
  }

  public searchLocation(text: string): Observable<HistoricalPlaceEntity[]> {
    const params = new HttpParams().set('query', text);
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + 'api/search/searchPlaces', {
      params: params
    });
  }

  public searchPersons(text: string): Observable<HistoricalPersonEntity[]> {
    const params = new HttpParams().set('query', text);
    return this.httpClient.get<HistoricalPersonEntity[]>(BACKEND_ADDRESS + 'api/search/searchPersons', {
      params: params
    });
  }
}
