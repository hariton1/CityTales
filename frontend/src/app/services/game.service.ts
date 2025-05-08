import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {GameInterface} from '../dto/game.interface';
import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class GameService {

  constructor(private httpClient: HttpClient) {
  }

  public createGame(gameDto: GameInterface): Observable<GameInterface> {
    return this.httpClient.post<GameInterface>(SERVER_ADDRESS + 'game/create', {
      params: [],
      data: gameDto
    });
  }

  public readGame(gameId: number): Observable<GameInterface> {
    return this.httpClient.get<GameInterface>(SERVER_ADDRESS + 'game/read' + gameId);
  }

  public updateGame(gameDto: GameInterface): Observable<GameInterface> {
    return this.httpClient.put<GameInterface>(SERVER_ADDRESS + 'game/update', {
      params: [],
      data: gameDto
    });
  }

  public deleteGame(gameId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'game/delete/' + gameId);
  }
}
