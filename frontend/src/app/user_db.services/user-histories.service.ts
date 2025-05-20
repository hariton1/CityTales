import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {UUID} from 'node:crypto';
import {UserHistoryDto} from '../user_db.dto/user-history.dto';

@Injectable({providedIn: 'root'})
export class UserHistoriesService {

  private DOMAIN = SERVER_ADDRESS + 'userHistories/';

  constructor(private httpClient: HttpClient) {
  }

  public getAllUserHistories(): Observable<UserHistoryDto[]> {
    return this.httpClient.get<UserHistoryDto[]>(this.DOMAIN);
  }

  public getUserHistoriesById(user_history_id: number): Observable<UserHistoryDto> {
    return this.httpClient.get<UserHistoryDto>(this.DOMAIN + 'id=' + user_history_id);
  }

  public getUserHistoriesByUserId(user_id: UUID): Observable<UserHistoryDto[]> {
    return this.httpClient.get<UserHistoryDto[]>(this.DOMAIN + 'user_id=' + user_id);
  }

  public getUserHistoriesByArticleId(article_id: number): Observable<UserHistoryDto[]> {
    return this.httpClient.get<UserHistoryDto[]>(this.DOMAIN + 'article_id=' + article_id);
  }

  public createNewUserHistory(user_interest: UserHistoryDto) {
    this.httpClient.post(this.DOMAIN + 'create', {
      body: JSON.stringify(user_interest)
    })
  }

  public updateUserHistory(user_interest: UserHistoryDto) {
    this.httpClient.put(this.DOMAIN + 'update', {
      body: JSON.stringify(user_interest)
    })
  }
}
