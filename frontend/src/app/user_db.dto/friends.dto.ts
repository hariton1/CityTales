import {UUID} from 'node:crypto';

export class FriendsDto {
  public friends_id: number;
  public friend_one: UUID;
  public friend_two: UUID;
  public cre_dat: Date;

  constructor(friends_id: number, friend_one: UUID, friend_two: UUID, creDat: Date) {
    this.friends_id = friends_id;
    this.friend_one = friend_one;
    this.friend_two = friend_two;
    this.cre_dat = creDat;
  }

  public getFriendsId(): number {
    return this.friends_id;
  }

  public setFriendsId(value: number): void {
    this.friends_id = value;
  }

  public getFriendOne(): UUID {
    return this.friend_one;
  }

  public setFriendOne(value: UUID): void {
    this.friend_one = value;
  }

  public getFriendTwo(): UUID {
    return this.friend_two;
  }

  public setFriendTwo(value: UUID): void {
    this.friend_two = value;
  }

  public getCreDat(): Date {
    return this.cre_dat;
  }

  public setCreDat(value: Date): void {
    this.cre_dat = value;
  }
}
