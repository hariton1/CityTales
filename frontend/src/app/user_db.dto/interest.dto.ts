export class InterestDto {
  private interest_id: number;
  private interest_name: string;
  private description: string;
  private interest_name_de: string;

  constructor(interest_id: number, interest_name: string, description: string, interest_name_de: string) {
    this.interest_id = interest_id;
    this.interest_name = interest_name;
    this.description = description;
    this.interest_name_de = interest_name_de;
  }

  public getInterestId(): number {
    return this.interest_id;
  }

  public setInterestId(value: number): void {
    this.interest_id = value;
  }

  public getInterestName(): string {
    return this.interest_name;
  }

  public setInterestName(value: string): void {
    this.interest_name = value;
  }

  public getInterestNameDe(): string {
    return this.interest_name_de;
  }

  public setInterestNameDe(value: string): void {
    this.interest_name_de = value;
  }

  public getDescription(): string {
    return this.description;
  }

  public setDescription(value: string): void {
    this.description = value;
  }
}
