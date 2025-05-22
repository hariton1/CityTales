import {Injectable} from '@angular/core';
import {SERVER_ADDRESS} from '../globals';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {UserDto} from '../user_db.dto/user.dto';

@Injectable({providedIn: 'root'})
export class UserService {
  readonly DOMAIN = SERVER_ADDRESS + 'users';

  constructor(readonly httpClient: HttpClient) {
  }

  public getAllUsers(): Observable<UserDto[]> {
    return this.httpClient.get<UserDto[]>(this.DOMAIN)
      .pipe(
      map(userList => userList.map(user => {
        return new UserDto(user.id, user.supabase_id, user.email, user.created_at, user.display_name, user.is_active);
      }))
    );
  }

  public getUserById(id: string) : Observable<UserDto> {
    return this.httpClient.get<UserDto>(`${this.DOMAIN}/id=${id}`)
      .pipe(map(user => {
        return new UserDto(user.id, user.supabase_id, user.email, user.created_at, user.display_name, user.is_active);
      }));
  }

  public deleteUserById(id: string) : Observable<void> {
    return this.httpClient.delete<void>(`${this.DOMAIN}/id=${id}`);
  }

}
