import {UUID} from 'node:crypto';

export class UserPointDto {
  private user_point_id: number;
  private user_id: UUID;
  private points: number;
  private earnedAt: Date;

  constructor(user_point_id: number, user_id: UUID, points: number, earnedAt: Date) {
    this.user_point_id = user_point_id;
    this.user_id = user_id;
    this.points = points;
    this.earnedAt = earnedAt;
  }

  public getUserPointId(): number {
    return this.user_point_id;
  }

  public setUserPointId(value: number): void {
    this.user_point_id = value;
  }

  public getUserId(): UUID {
    return this.user_id;
  }

  public setUserId(value: UUID): void {
    this.user_id = value;
  }

  public getPoints(): number {
    return this.points;
  }

  public setPoints(value: number): void {
    this.points = value;
  }

  public getEarnedAt(): Date {
    return this.earnedAt;
  }

  public setEarnedAt(value: Date): void {
    this.earnedAt = value;
  }
}
