import {Injectable} from '@angular/core';
import {SERVER_ADDRESS} from '../globals';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserBadgeDTO} from '../user_db.dto/user-badge.dto';
import {UserPointDto} from '../user_db.dto/user-point.dto';
import {UtilitiesService} from '../services/utilities.service';
import {UUID} from 'node:crypto';

@Injectable({providedIn: 'root'})
export class UserBadgesService {
  private DOMAIN = SERVER_ADDRESS + 'userBadges';

  constructor(private httpClient: HttpClient,
              private utilitiesService: UtilitiesService) {
  }

  getAllUserBadges(): Observable<UserBadgeDTO[]> {
    return this.httpClient.get<UserBadgeDTO[]>(this.DOMAIN);
  }

  getBadgesByUserId(userId: UUID): Observable<UserBadgeDTO[]> {
    return this.httpClient.get<UserBadgeDTO[]>(`${this.DOMAIN}/user_id=${userId}`);
  }

  getBadgesByArticleId(articleId: number): Observable<UserBadgeDTO[]> {
    return this.httpClient.get<UserBadgeDTO[]>(`${this.DOMAIN}/article_id=${articleId}`);
  }

  createUserBadge(badge: UserBadgeDTO): Observable<UserBadgeDTO> {
    const userBadgeToSend = {
      user_id: badge.getUserId(),
      article_id: badge.getArticleId(),
      earned_at: this.utilitiesService.formatDate(badge.getEarnedAt()),
    };

    return this.httpClient.post<UserBadgeDTO>(
      this.DOMAIN + '/create',
      userBadgeToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }
}
