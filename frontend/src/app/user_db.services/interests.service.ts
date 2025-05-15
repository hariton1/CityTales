import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {InterestDto} from '../user_db.dto/interest.dto';

@Injectable({providedIn: 'root'})
export class InterestsService {

  private DOMAIN = SERVER_ADDRESS + 'interests/';

  constructor(private httpClient: HttpClient) {
  }

  public getAllInterests(): Observable<InterestDto[]> {
    return this.httpClient.get<InterestDto[]>(this.DOMAIN);
  }

  public getInterestByInterestId(interest_id: number): Observable<InterestDto> {
    return this.httpClient.get<InterestDto>(this.DOMAIN + 'id=' + interest_id);
  }

  public getInterestByName(name: string): Observable<InterestDto> {
    return this.httpClient.get<InterestDto>(this.DOMAIN + 'name=' + name);
  }

  public getInterestsByDescriptionLike(description: string): Observable<InterestDto[]> {
    return this.httpClient.get<InterestDto[]>(this.DOMAIN + 'description=' + description);
  }

  public createNewInterest(interest: InterestDto) {
    this.httpClient.post(this.DOMAIN + 'create', {
      body: JSON.stringify(interest)
    })
  }
}
