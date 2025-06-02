import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Combined} from '../dto/db_entity/Combined';

@Injectable({ providedIn: 'root' })
export class CombinedService {

  private LOCATION_PATH = 'api/combined/'

  constructor(private httpClient: HttpClient) {
  }

  public getInfosById(id: number): Observable<Combined> {
    return this.httpClient.get<Combined>(BACKEND_ADDRESS + this.LOCATION_PATH + 'id/' + id);
  }
}
