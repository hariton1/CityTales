import {Injectable} from '@angular/core';
import {QDRANT_ADDRESS} from '../globals';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class QdrantService {

  private DOMAIN = QDRANT_ADDRESS + '/categorize';

  constructor(private httpClient: HttpClient) {
  }

  public getFilteredHistoryEntities(interestNames: string[], colName: String): Observable<number[]> {
    const matchRequestDto = {
      interests: interestNames,
      collectionName: colName,
      resultSize: 100
    };

    return this.httpClient.post<number[]>(
      this.DOMAIN + '/match',
      matchRequestDto,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }

}
