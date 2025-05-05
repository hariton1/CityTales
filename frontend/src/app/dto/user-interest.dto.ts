import {UserAccountDto} from './user-account.dto';

export class UserInterestDto {
  private readonly user: UserAccountDto;
  private interestId: number;
  private readonly creationDate: number;

  constructor(user: UserAccountDto, interestId: number) {
    this.user = user;
    this.interestId = interestId;
    this.creationDate = Date.now();
  }

  setInterestId(interestId: number) {
    this.interestId = interestId;
  }

  getUser(): UserAccountDto {
    return this.user;
  }

  getInterestId(): number {
    return this.interestId;
  }

  getCreationDate(): number {
    return this.creationDate;
  }
}
