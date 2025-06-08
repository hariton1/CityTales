import {UUID} from 'node:crypto';

export class UserDataDto {
  private user_data_id: number;
  private user_id: UUID;
  private role_name: string;
  private status: string;
  private cre_dat: Date;

  constructor(user_data_id: number, user_id: UUID, role_name: string, status: string, creDat: Date) {
    this.user_data_id = user_data_id;
    this.user_id = user_id;
    this.role_name = role_name;
    this.status = status;
    this.cre_dat = creDat;
  }

  public getUserDataId(): number {
    return this.user_data_id;
  }

  public setUserDataId(value: number): void {
    this.user_data_id = value;
  }

  public getUserId(): UUID {
    return this.user_id;
  }

  public setUserId(value: UUID): void {
    this.user_id = value;
  }

  public getRoleName(): string {
    return this.role_name;
  }

  public setRoleName(value: string): void {
    this.role_name = value;
  }

  public getStatus(): string {
    return this.status;
  }

  public setStatus(value: string): void {
    this.status = value;
  }

  public getCreDat(): Date {
    return this.cre_dat;
  }

  public setCreDat(value: Date): void {
    this.cre_dat = value;
  }

  public toString(): string {
    return this.user_data_id + ' ' + this.user_id + ' ' + this.role_name + ' ' + this.status + ' ' + this.cre_dat;
  }
}
