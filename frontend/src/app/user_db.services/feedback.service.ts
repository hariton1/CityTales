import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {Injectable} from '@angular/core';
import {FeedbackDto} from '../user_db.dto/feedback.dto';
import {UUID} from 'node:crypto';
import {UtilitiesService} from '../services/utilities.service';
import {UserInterestDto} from '../user_db.dto/user-interest.dto';

@Injectable({providedIn: 'root'})
export class FeedbackService {

  private DOMAIN = SERVER_ADDRESS + 'feedbacks';

  constructor(private httpClient: HttpClient,
              private utilitiesService: UtilitiesService) {
  }

  public getAllFeedbacks(): Observable<FeedbackDto[]> {
    return this.httpClient.get<any[]>(this.DOMAIN)
      .pipe(
        map(data => data.map(item => {
          return new FeedbackDto(item.feedback_id, item.user_id, item.article_id, item.rating, item.fb_content, item.cre_dat);
        }))
      );
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

  public approveFeedback(feedback_id: number) {
    return this.httpClient.put(this.DOMAIN + '/approve/id=' + feedback_id, {});
  }

  public deleteFeedback(feedback_id: number) {
    return this.httpClient.delete(this.DOMAIN + '/delete/id=' + feedback_id);
  }
}
