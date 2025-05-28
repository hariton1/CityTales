import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
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

  public createNewUserHistory(user_history: UserHistoryDto): Observable<UserHistoryDto> {
    // Get the Date object
    const date = user_history.getOpenDt();

    // Format as YYYY-MM-DDTHH:mm:ss.000+00:00
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    // Format the date string exactly as required by the backend
    const formattedDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000+02:00`;

    const userHistoryToSend = {
      user_id: user_history.getUserId(),
      interest_id: user_history.getInterestId(),
      open_dt: formattedDate,
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
    // Get the Date object
    const openDate = user_history.getOpenDt();
    const closeDate = user_history.getOpenDt();

    // Format as YYYY-MM-DDTHH:mm:ss.000+00:00
    const year = openDate.getFullYear();
    const yearC = closeDate.getFullYear();
    const month = String(openDate.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
    const monthC = String(closeDate.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
    const day = String(openDate.getDate()).padStart(2, '0');
    const dayC = String(closeDate.getDate()).padStart(2, '0');
    const hours = String(openDate.getHours()).padStart(2, '0');
    const hoursC = String(closeDate.getHours()).padStart(2, '0');
    const minutes = String(openDate.getMinutes()).padStart(2, '0');
    const minutesC = String(closeDate.getMinutes()).padStart(2, '0');
    const seconds = String(openDate.getSeconds()).padStart(2, '0');
    const secondsC = String(closeDate.getSeconds()).padStart(2, '0');

    // Format the date string exactly as required by the backend
    const formattedOpenDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000+02:00`;
    const formattedCloseDate = `${yearC}-${monthC}-${dayC}T${hoursC}:${minutesC}:${secondsC}.000+02:00`;

    const userHistoryToSend = {
      user_history_id: user_history.getUserHistoryId(),
      user_id: user_history.getUserId(),
      interest_id: user_history.getInterestId(),
      open_dt: formattedOpenDate,
      close_dt: formattedCloseDate,
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
