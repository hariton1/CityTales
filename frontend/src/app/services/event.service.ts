import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BACKEND_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {HistoricalEventEntity} from '../dto/db_entity/HistoricalEventEntity';
import {HistoricalPlaceEntity} from '../dto/db_entity/HistoricalPlaceEntity';
import {HistoricalPersonEntity} from '../dto/db_entity/HistoricalPersonEntity';

@Injectable({ providedIn: 'root' })
export class EventService {

  private EVENT_PATH = 'api/historicEvent/'

  constructor(private httpClient: HttpClient) {
  }


  public getEventByVHWId(id: number): Observable<HistoricalEventEntity> {
    return this.httpClient.get<HistoricalEventEntity>(BACKEND_ADDRESS + this.EVENT_PATH + 'by/id/' + id.toString());
  }

  public getEventByPartialName(name: string): Observable<HistoricalEventEntity[]> {
    return this.httpClient.get<HistoricalEventEntity[]>(BACKEND_ADDRESS + this.EVENT_PATH + 'by/name/' + name);
  }

  public getLinkedEventsByVHWikiId(id: number): Observable<HistoricalEventEntity[]> {
    return this.httpClient.get<HistoricalEventEntity[]>(BACKEND_ADDRESS + this.EVENT_PATH + 'links/by/id/' + id);
  }

  public getLinkedLocations(viennaHistoryWikiId: string): Observable<HistoricalPlaceEntity[]> {
    return this.httpClient.get<HistoricalPlaceEntity[]>(BACKEND_ADDRESS + this.EVENT_PATH + 'links/buildings/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedPersons(viennaHistoryWikiId: string): Observable<HistoricalPersonEntity[]> {
    return this.httpClient.get<HistoricalPersonEntity[]>(BACKEND_ADDRESS + this.EVENT_PATH + 'links/persons/by/id/' + viennaHistoryWikiId);
  }

  public getLinkedEvents(viennaHistoryWikiId: string): Observable<HistoricalEventEntity[]> {
    return this.httpClient.get<HistoricalEventEntity[]>(BACKEND_ADDRESS + this.EVENT_PATH + 'links/events/by/id/' + viennaHistoryWikiId);
  }
}
