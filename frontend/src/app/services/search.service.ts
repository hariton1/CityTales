import {HttpClient} from '@angular/common/http';
import { HttpParams } from '@angular/common/http';

import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {LocationDto} from '../dto/location.dto';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';

@Injectable({ providedIn: 'root' })
export class SearchService {

  constructor(private httpClient: HttpClient) {
  }

  public searchLocation(text: string): Observable<HistoricalPlaceEntity[]> {
    const params = new HttpParams().set('query', text);
    return this.httpClient.get<HistoricalPlaceEntity[]>('http://localhost:8083/api/search/searchPlaces', {
      params: params
    });
  }
}
