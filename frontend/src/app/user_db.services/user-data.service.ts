import {Injectable} from '@angular/core';
import {SERVER_ADDRESS} from '../globals';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UtilitiesService} from '../services/utilities.service';
import {map, Observable} from 'rxjs';
import {UserDataDto} from '../user_db.dto/user-data.dto';
import {UUID} from 'node:crypto';

@Injectable({providedIn: 'root'})
export class UserDataService {

  private DOMAIN = SERVER_ADDRESS + 'userData';

  constructor(private httpClient: HttpClient,
              private utilitiesService: UtilitiesService) {
  }

  public getAllUserData(): Observable<UserDataDto[]> {
    return this.httpClient.get<any[]>(this.DOMAIN)
      .pipe(
        map(data => data.map(item => {
          return new UserDataDto(item.user_data_id, item.user_id, item.role_name, item.status, item.cre_dat);
        }))
      );
  }

  public getUserDataByUserId(user_id: UUID): Observable<UserDataDto> {
    return this.httpClient.get<any>(this.DOMAIN + '/user_id=' + user_id)
      .pipe(
        map(item => {
          return new UserDataDto(item.user_data_id, item.user_id, item.role_name, item.status, item.cre_dat);
        })
      );
  }

  public saveUserData(userData: UserDataDto): Observable<UserDataDto> {

    const userDataToSend = {
      user_data_id: userData.getUserDataId(),
      user_id: userData.getUserId(),
      role_name: userData.getRoleName(),
      status: userData.getStatus(),
      cre_dat: this.utilitiesService.formatDate(userData.getCreDat())
    };

    return this.httpClient.post<UserDataDto>(
      this.DOMAIN + '/save',
      userDataToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }

}
