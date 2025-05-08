import {UserAccountDto} from './user-account.dto';

export class TourDto {
  private readonly user: UserAccountDto;
  private readonly locationIds: string[];

  constructor(user: UserAccountDto) {
    this.user = user;
    this.locationIds = [];
  }

  addLocation(locationId: string) {
    this.locationIds.push(locationId);
  }

  removeLocation(locationId: string) {
    this.locationIds.splice(this.locationIds.indexOf(locationId), 1);
  }

  getUser(): UserAccountDto {
    return this.user;
  }

  getLocations(): string[] {
    return this.locationIds;
  }
}
