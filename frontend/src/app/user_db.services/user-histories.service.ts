import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {UUID} from 'node:crypto';
import {UserHistoryDto} from '../user_db.dto/user-history.dto';
import {UtilitiesService} from '../services/utilities.service';

@Injectable({providedIn: 'root'})
export class UserHistoriesService {

  private DOMAIN = SERVER_ADDRESS + 'userHistories/';

  constructor(private httpClient: HttpClient,
              private utilitiesService: UtilitiesService) {
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

  public createNewUserHistory(user_history: UserHistoryDto): Observable<UserHistoryDto> {

    const userHistoryToSend = {
      user_id: user_history.getUserId(),
      interest_id: user_history.getInterestId(),
      open_dt: this.utilitiesService.formatDate(user_history.getOpenDt()),
      article_id: user_history.getArticleId()
    };

    return this.httpClient.post<any>(
      this.DOMAIN + 'create',
      userHistoryToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    ).pipe(
      map(response => new UserHistoryDto(
        response.user_history_id,
        response.user_id,
        response.article_id,
        response.open_dt,
        response.close_dt,
        response.interest_id
      ))

    );
  }

  public updateUserHistory(user_history: UserHistoryDto) {

    const userHistoryToSend = {
      user_history_id: user_history.getUserHistoryId(),
      user_id: user_history.getUserId(),
      interest_id: user_history.getInterestId(),
      open_dt: this.utilitiesService.formatDate(user_history.getOpenDt()),
      close_dt: this.utilitiesService.formatDate(user_history.getCloseDt()),
      article_id: user_history.getArticleId()
    };

    return this.httpClient.put<UserHistoryDto>(
      this.DOMAIN + 'update',
      userHistoryToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }
}
