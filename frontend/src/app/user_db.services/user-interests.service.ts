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
    // this.httpClient.get<UserInterestDto[]>(this.DOMAIN + 'user_id=' + user_id);
    return this.httpClient.get<any[]>(this.DOMAIN + 'user_id=' + user_id)
        .pipe(
            map(data => data.map(item => {
              return new UserInterestDto(item.user, item.interest_id, item.cre_dat, item.interest_weight);
            }))
        );
  }

  public getUserInterestsByInterestId(interest_id: number): Observable<UserInterestDto[]> {
    return this.httpClient.get<UserInterestDto[]>(this.DOMAIN + 'interest_id=' + interest_id);
  }

  public createNewUserInterest(user_interest: UserInterestDto): Observable<UserInterestDto> {
    // Get the Date object
    const date = user_interest.getCreDat();

    // Format as YYYY-MM-DDTHH:mm:ss.000+00:00
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    // Format the date string exactly as required by the backend
    const formattedDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000+02:00`;

    const userInterestToSend = {
      user_id: user_interest.getUserId(),
      interest_id: user_interest.getInterestId(),
      cre_dat: formattedDate,
      interest_weight: user_interest.getInterestWeight()
    };

    return this.httpClient.post<UserInterestDto>(
      this.DOMAIN + 'create',
      userInterestToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
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
