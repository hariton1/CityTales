import {UUID} from 'node:crypto';

export class UserHistoryDto {
  private user_history_id: number;
  private user_id: UUID;
  private article_id: number;
  private openDt: Date;
  private closeDt: Date;
  private interest_id: number;

  constructor(user_history_id: number, user_id: UUID, article_id: number, openDt: Date, closeDt: Date, interest_id: number) {
    this.user_history_id = user_history_id;
    this.user_id = user_id;
    this.article_id = article_id;
    this.openDt = openDt;
    this.closeDt = closeDt;
    this.interest_id = interest_id;
  }

  public getUserHistoryId(): number {
    return this.user_history_id;
  }

  public setUserHistoryId(value: number): void {
    this.user_history_id = value;
  }

  public getUserId(): UUID {
    return this.user_id;
  }

  public setUserId(value: UUID): void {
    this.user_id = value;
  }

  public getArticleId(): number {
    return this.article_id;
  }

  public setArticleId(value: number): void {
    this.article_id = value;
  }

  public getOpenDt(): Date {
    return this.openDt;
  }

  public setOpenDt(value: Date): void {
    this.openDt = value;
  }

  public getCloseDt(): Date {
    return this.closeDt;
  }

  public setCloseDt(value: Date): void {
    this.closeDt = value;
  }

  public getInterestId(): number {
    return this.interest_id;
  }

  public setInterestId(value: number): void {
    this.interest_id = value;
  }
}
