import {UUID} from 'node:crypto';

export class UserPointDto {
  public user_point_id: number;
  public user_id: UUID;
  public points: number;
  public cre_dat: Date;

  constructor(user_point_id: number, user_id: UUID, points: number, cre_dat: Date) {
    this.user_point_id = user_point_id;
    this.user_id = user_id;
    this.points = points;
    this.cre_dat = cre_dat;
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
    return this.cre_dat;
  }

  public setEarnedAt(value: Date): void {
    this.cre_dat = value;
  }
}
