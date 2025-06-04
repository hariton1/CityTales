import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {FeedbackDto} from '../user_db.dto/feedback.dto';
import {UUID} from 'node:crypto';
import {UtilitiesService} from '../services/utilities.service';

@Injectable({providedIn: 'root'})
export class FeedbackService {

  private DOMAIN = SERVER_ADDRESS + 'feedbacks';

  constructor(private httpClient: HttpClient,
              private utilitiesService: UtilitiesService) {
  }

  public getAllFeedbacks(): Observable<FeedbackDto[]> {
    return this.httpClient.get<FeedbackDto[]>(this.DOMAIN);
  }

  public getFeedbackById(feedback_id: number): Observable<FeedbackDto> {
    return this.httpClient.get<FeedbackDto>(this.DOMAIN + '/id=' + feedback_id);
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

    const feedbackToSend = {
      user_id: feedback.getUserId(),
      article_id: feedback.getArticleId(),
      cre_dat: this.utilitiesService.formatDate(feedback.getCreDat()),
      rating: feedback.getRating(),
      fb_content: feedback.getFbContent()
    };

    return this.httpClient.post<FeedbackDto>(
      this.DOMAIN + '/create',
      feedbackToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }
}
