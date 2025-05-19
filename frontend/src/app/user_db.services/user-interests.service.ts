import {HttpClient, HttpHeaders} from '@angular/common/http';
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

  public deleteUserInterest(user_interest: UserInterestDto): Observable<any> {
    let formattedDate: string;
    const creDat = user_interest.getCreDat();

    const dateMatch = String(creDat).match(/(.+?)(?:\s+[AP]M)?$/);
    formattedDate = dateMatch ? dateMatch[1] : String(creDat);

    /*
    if (typeof creDat === 'string') {
      console.log('part 1');
      const dateMatch = String(creDat).match(/(.+?)(?:\s+[AP]M)?$/);
      formattedDate = dateMatch ? dateMatch[1] : String(creDat);
      // If it's already a string, extract the date part without the "PM"/"AM" suffix
      // This regex extracts everything before any AM/PM suffix

    } else if (creDat instanceof Date) {
      console.log('part 2');
      // If it's a Date object, convert to ISO string and take first 19 chars
      formattedDate = creDat.toISOString().substring(0, 27);
      console.log(formattedDate);
    } else {
      console.log('part 3');
      // Fallback for any other case - just convert to string
      formattedDate = String(creDat);
    } */

    const userInterestToSend = {
      user_id: user_interest.getUserId(),
      interest_id: user_interest.getInterestId(),
      cre_dat: formattedDate.concat('+00:00'), // Format as YYYY-MM-DDTHH:mm:ss
      interest_weight: user_interest.getInterestWeight()
    };

    //console.log('userinterest' + user_interest.toString());
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      body: userInterestToSend
    };
    return this.httpClient.delete(this.DOMAIN + 'delete', options);
  }

}
