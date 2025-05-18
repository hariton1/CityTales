import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS, SERVER_ADDRESS} from '../globals';
import {LocationDto} from '../dto/location.dto';
import {Injectable} from '@angular/core';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';
import {HistoricalPersonEntity} from '../dto/db_entity/HistoricalPersonEntity';

@Injectable({ providedIn: 'root' })
export class PersonService {

  private PERSON_PATH = 'api/historicPerson/'

  constructor(private httpClient: HttpClient) {
  }

  public getPersonById(id: number): Observable<HistoricalPersonEntity> {
    return this.httpClient.get<HistoricalPersonEntity>(BACKEND_ADDRESS + this.PERSON_PATH + 'by/id/' + id.toString());
  }

  public getPersonByPartialName(name: string): Observable<HistoricalPersonEntity> {
    return this.httpClient.get<HistoricalPersonEntity>(BACKEND_ADDRESS + this.PERSON_PATH + 'by/name/' + name);
  }


  //Get by Vienna History Wiki ID
  public getLinkedPersonsByVHWikiId(id: number): Observable<HistoricalPersonEntity[]> {
    return this.httpClient.get<HistoricalPersonEntity[]>(BACKEND_ADDRESS + this.PERSON_PATH + 'links/by/id/' + id);
  }
}
