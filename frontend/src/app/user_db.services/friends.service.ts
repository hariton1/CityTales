import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {FriendsDto} from '../user_db.dto/friends.dto';
import {UUID} from 'node:crypto';

@Injectable({providedIn: 'root'})
export class FriendsService {

  private DOMAIN = SERVER_ADDRESS + 'friends/';

  constructor(private httpClient: HttpClient) {
  }

  public getAllFriends(): Observable<FriendsDto[]> {
    return this.httpClient.get<FriendsDto[]>(this.DOMAIN);
  }

  public getFriendsById(friends_id: number): Observable<FriendsDto> {
    return this.httpClient.get<FriendsDto>(this.DOMAIN + 'id=' + friends_id);
  }

  public getFriendsByFriendOne(friend_one: string): Observable<FriendsDto[]> {
    return this.httpClient.get<FriendsDto[]>(this.DOMAIN + 'friend_one=' + friend_one);
  }

  public getFriendsByFriendTwo(friend_two: string): Observable<FriendsDto[]> {
    return this.httpClient.get<FriendsDto[]>(this.DOMAIN + 'friend_two=' + friend_two);
  }

  public createNewFriendsPair(friend: FriendsDto): Observable<any> {
    return this.httpClient.post(this.DOMAIN + 'create', friend);
  }

  public deleteFriendsPair(friend: FriendsDto) {
    return this.httpClient.request('DELETE', this.DOMAIN + 'delete', {
      body: friend,
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
}
