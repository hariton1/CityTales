import {UserAccountDto} from './user-account.dto';

export class SearchHistoryDTO {

  private readonly user: UserAccountDto;
  private searchTerms: string[];
  private visitedLocationIds: string[];
  private visitedStoryIds: string[];

  constructor(user: UserAccountDto) {
    this.user = user;
    this.searchTerms = [];
    this.visitedLocationIds = [];
    this.visitedStoryIds = [];
  }

  setSearchTerms(searchTerms: string[]) {
    this.searchTerms = searchTerms;
  }

  setVisitedLocationIds(locationIds: string[]) {
    this.visitedLocationIds = locationIds;
  }

  setVisitedStoryIds(storyIds: string[]) {
    this.visitedStoryIds = storyIds;
  }

  getSearchTerms(): string[] {
    return this.searchTerms;
  }

  getVisitedLocationIds(): string[] {
    return this.visitedLocationIds;
  }

  getVisitedStoryIds(): string[] {
    return this.visitedStoryIds;
  }
}
