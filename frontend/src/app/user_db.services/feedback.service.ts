import {HttpClient, HttpHeaders} from '@angular/common/http';
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
    // Get the Date object
    const date = feedback.getCreDat();

    // Format as YYYY-MM-DDTHH:mm:ss.000+00:00
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    // Format the date string exactly as required by the backend
    const formattedDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000+02:00`;

    const feedbackToSend = {
      user_id: feedback.getUserId(),
      article_id: feedback.getArticleId(),
      cre_dat: formattedDate,
      rating: feedback.getRating(),
      fb_content: feedback.getFbContent()
    };

    return this.httpClient.post<FeedbackDto>(
      this.DOMAIN + 'create',
      feedbackToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }
}
