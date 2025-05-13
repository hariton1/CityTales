import {UserAccountDto} from './user-account.dto';
import {GameInterface} from './game.interface';

export class GamificationDto {
  private readonly id: number;
  private user: UserAccountDto;
  private game: GameInterface;

  constructor(id: number, user: UserAccountDto, game: GameInterface) {
    this.id = id;
    this.user = user;
    this.game = game;
  }

  setUser(user: UserAccountDto) {
    this.user = user;
  }

  setGame(game: GameInterface) {
    this.game = game;
  }

  getUser(): UserAccountDto {
    return this.user;
  }

  getGame() {
    return this.game;
  }
}
