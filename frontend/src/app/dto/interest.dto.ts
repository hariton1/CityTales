export class InterestDto {
  protected interestId: number;
  private interestName: string;
  private description: string;

  constructor( interestId: number, interestName: string, description: string) {
    this.interestId = interestId;
    this.interestName = interestName;
    this.description = description;
  }

  getInterestId(): number {
    return this.interestId;
  }

  getInterestName(): string {
    return this.interestName;
  }

  getDescription(): string {
    return this.description;
  }

}
