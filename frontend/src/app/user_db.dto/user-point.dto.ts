import {UUID} from 'node:crypto';

export class UserPointDto {
  public user_point_id: number;
  public user_id: UUID;
  public points: number;
  public earned_at: Date;

  constructor(user_point_id: number, user_id: UUID, points: number, earned_at: Date) {
    this.user_point_id = user_point_id;
    this.user_id = user_id;
    this.points = points;
    this.earned_at = earned_at;
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
    return this.earned_at;
  }

  public setEarnedAt(value: Date): void {
    this.earned_at = value;
  }
}
