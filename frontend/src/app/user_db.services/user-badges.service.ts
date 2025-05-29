import {Injectable} from '@angular/core';
import {SERVER_ADDRESS} from '../globals';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserBadgeDTO} from '../user_db.dto/user-badge.dto';

@Injectable({providedIn: 'root'})
export class UserBadgesService {
  private DOMAIN = SERVER_ADDRESS + 'userBadges/';

  constructor(private httpClient: HttpClient) {
  }

  getAllUserBadges(): Observable<UserBadgeDTO[]> {
    return this.httpClient.get<UserBadgeDTO[]>(this.DOMAIN);
  }

  getBadgesByUserId(userId: string): Observable<UserBadgeDTO[]> {
    return this.httpClient.get<UserBadgeDTO[]>(`${this.DOMAIN}user_id=${userId}`);
  }

  getBadgesByArticleId(articleId: number): Observable<UserBadgeDTO[]> {
    return this.httpClient.get<UserBadgeDTO[]>(`${this.DOMAIN}article_id=${articleId}`);
  }

  createUserBadge(badge: UserBadgeDTO): Observable<UserBadgeDTO> {
    return this.httpClient.post<UserBadgeDTO>(`${this.DOMAIN}create`, badge);
  }
}
