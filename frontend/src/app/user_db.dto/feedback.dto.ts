import {UUID} from 'node:crypto';

export class FeedbackDto {
  private feedback_id: number;
  private user_id: UUID;
  private article_id: number;
  private rating: number;
  private fb_content: string;
  private creDat: Date;

  constructor(feedback_id: number, user_id: UUID, article_id: number, rating: number, fb_content: string, creDat: Date) {
    this.feedback_id = feedback_id;
    this.user_id = user_id;
    this.article_id = article_id;
    this.rating = rating;
    this.fb_content = fb_content;
    this.creDat = creDat;
  }

  public getFeedbackId(): number {
    return this.feedback_id;
  }

  public setFeedbackId(value: number): void {
    this.feedback_id = value;
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

  public getRating(): number {
    return this.rating;
  }

  public setRating(value: number): void {
    this.rating = value;
  }

  public getFbContent(): string {
    return this.fb_content;
  }

  public setFbContent(value: string): void {
    this.fb_content = value;
  }

  public getCreDat(): Date {
    return this.creDat;
  }

  public setCreDat(value: Date): void {
    this.creDat = value;
  }
}
