import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {HistoricalPersonEntity} from '../dto/db_entity/HistoricalPersonEntity';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';
import {HistoricalEventEntity} from '../dto/db_entity/HistoricalEventEntity';

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
  public getLinkedPersons(viennaHistoryWikiId: string): Observable<HistoricalPersonEntity[]> {
    return this.httpClient.get<HistoricalPersonEntity[]>(BACKEND_ADDRESS + this.PERSON_PATH + 'links/persons/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedLocations(viennaHistoryWikiId: string): Observable<HistoricalPlaceEntity[]> {
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + this.PERSON_PATH + 'links/buildings/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedEvents(viennaHistoryWikiId: string): Observable<HistoricalEventEntity[]> {
    return this.httpClient.get<HistoricalEventEntity[]>(BACKEND_ADDRESS + this.PERSON_PATH + 'links/events/by/id/' + viennaHistoryWikiId);
  }
}
