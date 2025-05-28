import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {UUID} from 'node:crypto';
import {UserPointDto} from '../user_db.dto/user-point.dto';

@Injectable({providedIn: 'root'})
export class UserPointsService {

  private DOMAIN = SERVER_ADDRESS + 'userPoints';

  constructor(private httpClient: HttpClient) {
  }

  public getAllUserPoints(): Observable<UserPointDto[]> {
    return this.httpClient.get<UserPointDto[]>(this.DOMAIN);
  }

  public getUserPointById(user_history_id: number): Observable<UserPointDto> {
    return this.httpClient.get<UserPointDto>(this.DOMAIN + 'id=' + user_history_id);
  }

  public getUserPointsByUserId(user_id: UUID): Observable<UserPointDto[]> {
    return this.httpClient.get<UserPointDto[]>(this.DOMAIN + 'user_id=' + user_id);
  }

  public createNewPoints(user_point: UserPointDto) {
    this.httpClient.post(this.DOMAIN + 'create', {
      body: JSON.stringify(user_point)
    })
  }
}
