import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {UserInterestDto} from '../user_db.dto/user-interest.dto';
import {UUID} from 'node:crypto';

@Injectable({providedIn: 'root'})
export class UserInterestsService {

  private DOMAIN = SERVER_ADDRESS + 'userInterests/';

  constructor(private httpClient: HttpClient) {
  }

  public getAllUserInterests(): Observable<UserInterestDto[]> {
    return this.httpClient.get<UserInterestDto[]>(this.DOMAIN);
  }

  public getUserInterestsByUserId(user_id: UUID): Observable<UserInterestDto[]> {
    return this.httpClient.get<UserInterestDto[]>(this.DOMAIN + 'user_id=' + user_id);
  }

  public getUserInterestsByInterestId(interest_id: number): Observable<UserInterestDto[]> {
    return this.httpClient.get<UserInterestDto[]>(this.DOMAIN + 'interest_id=' + interest_id);
  }

  public createNewUserInterest(user_interest: UserInterestDto) {
    this.httpClient.post(this.DOMAIN + 'create', {
      body: JSON.stringify(user_interest)
    })
  }

  public deleteUserInterest(user_interest: UserInterestDto) {
    this.httpClient.delete(this.DOMAIN + 'delete', {
      body: JSON.stringify(user_interest)
    })
  }
}
