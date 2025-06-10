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
        return new UserDto(user.id, user.email, user.created_at, user.role, user.status);
      }))
    );
  }

  public getUserById(id: string) : Observable<UserDto> {
    return this.httpClient.get<UserDto>(`${this.DOMAIN}/id=${id}`)
      .pipe(map(user => {
        return new UserDto(user.id,user.email, user.created_at, user.role, user.status);
      }));
  }

  public getUserEmailById(id: string): Observable<string> {
    return this.httpClient.get(`${this.DOMAIN}/email/id=${id}`, { responseType: 'text' });
  }
  public deleteUserById(id: string) : Observable<void> {
    return this.httpClient.delete<void>(`${this.DOMAIN}/id=${id}`);
  }

  public updateUser(user: UserDto) : Observable<UserDto> {
    const payload = {
      id: user.id,
      email: user.email,
      role: user.role,
      status: user.status,
    };

    return this.httpClient.patch<UserDto>(`${this.DOMAIN}/id=${user.id}`, payload)
      .pipe(map(updatedUser => {
        return new UserDto(updatedUser.id, updatedUser.email, updatedUser.created_at, updatedUser.role, updatedUser.status);
      }));
  }
}
