import {HttpClient} from '@angular/common/http';
import { HttpParams } from '@angular/common/http';

import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {LocationDto} from '../dto/location.dto';
import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SearchService {

  constructor(private httpClient: HttpClient) {
  }

  public searchLocation(text: string): Observable<LocationDto[]> {
    const params = new HttpParams().set('latitude', 48.2321).set('longitude', 16.4278).set('radius', '1000');
    return this.httpClient.get<LocationDto[]>('localhost:8083' + '/api/historicPlace/by/location', {
      params: params
    });
  }
}
