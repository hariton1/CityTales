import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {FeedbackDto} from '../user_db.dto/feedback.dto';
import {UUID} from 'node:crypto';

@Injectable({providedIn: 'root'})
export class FeedbackService {

  private DOMAIN = SERVER_ADDRESS + 'feedbacks/';

  constructor(private httpClient: HttpClient) {
  }

  public getAllFeedbacks(): Observable<FeedbackDto[]> {
    return this.httpClient.get<FeedbackDto[]>(this.DOMAIN);
  }

  public getFeedbackById(feedback_id: number): Observable<FeedbackDto> {
    return this.httpClient.get<FeedbackDto>(this.DOMAIN + feedback_id);
  }

  public getFeedbacksByUserId(user_id: UUID): Observable<FeedbackDto> {
    return this.httpClient.get<FeedbackDto>(this.DOMAIN + '/user_id=' + user_id);
  }

  public getFeedbacksByArticleId(article_id: number): Observable<FeedbackDto[]> {
    return this.httpClient.get<FeedbackDto[]>(this.DOMAIN + '/article_id=' + article_id);
  }

  public getFeedbacksByFbContentLike(content: string): Observable<FeedbackDto[]> {
    return this.httpClient.get<FeedbackDto[]>(this.DOMAIN + '/content=' + content);
  }

  public createNewFeedback(feedback: FeedbackDto) {
    this.httpClient.post(this.DOMAIN + '/create', {
      body: JSON.stringify(feedback)
    });
  }
}
