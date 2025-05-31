import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {UUID} from 'node:crypto';
import {UserPointDto} from '../user_db.dto/user-point.dto';
import {UserInterestDto} from '../user_db.dto/user-interest.dto';
import {UtilitiesService} from '../services/utilities.service';

@Injectable({providedIn: 'root'})
export class UserPointsService {

  private DOMAIN = SERVER_ADDRESS + 'userPoints/';

  constructor(private httpClient: HttpClient,
              private utilitiesService: UtilitiesService) {
  }

  public getAllUserPoints(): Observable<UserPointDto[]> {
    return this.httpClient.get<UserPointDto[]>(this.DOMAIN);
  }

  public getUserPointById(user_history_id: number): Observable<UserPointDto> {
    return this.httpClient.get<UserPointDto>(this.DOMAIN + 'id=' + user_history_id);
  }

  public getUserPointsByUserId(user_id: string): Observable<UserPointDto[]> {
    return this.httpClient.get<UserPointDto[]>(this.DOMAIN + 'user_id=' + user_id);
  }

  public createNewPoints(user_point: UserPointDto): Observable<UserPointDto> {
    const userPointsToSend = {
      user_id: user_point.getUserId(),
      points: user_point.getPoints(),
      earned_at: this.utilitiesService.formatDate(user_point.getEarnedAt()),
    };

    return this.httpClient.post<UserPointDto>(
      this.DOMAIN + 'create',
      userPointsToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }
}
