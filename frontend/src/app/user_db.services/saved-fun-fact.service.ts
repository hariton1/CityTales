import {Injectable} from '@angular/core';
import {UtilitiesService} from '../services/utilities.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {SavedFunFactDto} from '../user_db.dto/saved-fun-fact.dto';
import {SERVER_ADDRESS} from '../globals';
import {UUID} from 'node:crypto';

@Injectable({providedIn: 'root'})
export class SavedFunFactService {

  private DOMAIN = SERVER_ADDRESS + 'funFacts';

  constructor(private httpClient: HttpClient,
              private utilitiesService: UtilitiesService) {
  }

  public getFunFactsByUserId(user_id:UUID): Observable<SavedFunFactDto[]> {

    return this.httpClient.get<any[]>(this.DOMAIN + '/user_id=' + user_id)
      .pipe(
        map(data => data.map(item => {
          return new SavedFunFactDto(
            item.saved_fun_fact_id,
            item.user_id,
            item.article_id,
            item.headline,
            item.fun_fact,
            item.image_url,
            item.score,
            item.reason);
        }))
      );

  }

  public createNewSavedFunFact(savedFunFact: SavedFunFactDto): Observable<SavedFunFactDto> {

    const savedFunFactToSend = {
      user_id: savedFunFact.getUserId(),
      article_id: savedFunFact.getArticleId(),
      headline: savedFunFact.getHeadline(),
      fun_fact: savedFunFact.getFunFact(),
      image_url: savedFunFact.getImageUrl(),
      score: savedFunFact.getScore(),
      reason: savedFunFact.getReason(),
    };

    return this.httpClient.post<SavedFunFactDto>(
      this.DOMAIN + '/create',
      savedFunFactToSend,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      }
    );
  }

  public deleteSavedFunFact(deletedFunFact: SavedFunFactDto): Observable<any>{

    const deletedFunFactToSend = {
      saved_fun_fact_id: deletedFunFact.getSavedFunFactId(),
      user_id: deletedFunFact.getUserId(),
      article_id: deletedFunFact.getArticleId(),
      headline: deletedFunFact.getHeadline(),
      fun_fact: deletedFunFact.getFunFact(),
      image_url: deletedFunFact.getImageUrl(),
      score: deletedFunFact.getScore(),
      reason: deletedFunFact.getReason(),
    };

    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      body: deletedFunFactToSend
    };

    return this.httpClient.delete(this.DOMAIN + '/delete', options);
  }

}
