import {UUID} from 'node:crypto';

export class UserBadgeDTO {
  private user_badge_id: number;
  private user_id: UUID;
  private article_id: number;
  private earned_at: Date;

  constructor(user_badge_id: number, user_id: UUID, article_id: number, earned_at: Date) {
    this.user_badge_id = user_badge_id;
    this.user_id = user_id;
    this.article_id = article_id;
    this.earned_at = earned_at;
  }

  public getUserBadgeId() {
    return this.user_badge_id;
  }

  public setUserBadgeId(value: number) {
    this.user_badge_id = value;
  }

  public getUserId() {
    return this.user_id;
  }

  public setUserId(value: UUID) {
    this.user_id = value;
  }

  public getArticleId() {
    return this.article_id;
  }

  public setArticleId(value: number) {
    this.article_id = value;
  }

  public getEarnedAt() {
    return this.earned_at;
  }

  public setEarnedAt(value: Date) {
    this.earned_at = value;
  }

}
