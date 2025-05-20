import {UUID} from 'node:crypto';

export class UserInterestDto {
  private user_id: UUID;
  private interest_id: number;
  private cre_dat: Date;
  private interest_weight: number;

  constructor(user_id: UUID, interest_id: number, creDat: Date, interest_weight: number) {
    this.interest_id = interest_id;
    this.user_id = user_id;
    this.cre_dat = creDat;
    this.interest_weight = interest_weight;
  }

  public getUserId(): UUID {
    return this.user_id;
  }

  public setUserId(value: UUID): void {
    this.user_id = value;
  }

  public getInterestId(): number {
    return this.interest_id;
  }

  public setInterestId(value: number): void {
    this.interest_id = value;
  }

  public getCreDat(): Date {
    return this.cre_dat;
  }

  public setCreDat(value: Date): void {
    this.cre_dat = value;
  }

  public getInterestWeight(): number {
    return this.interest_weight;
  }

  public setInterestWeight(value: number): void {
    this.interest_weight = value;
  }

  public toString(): string {
    return this.user_id + ' ' + this.interest_id + ' ' + this.cre_dat + ' ' + this.interest_weight;
  }
}
