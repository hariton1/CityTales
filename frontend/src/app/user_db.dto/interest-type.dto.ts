export class InterestTypeDto {
  private interest_type_id: number;
  private type_name: string;
  private description: string;
  private creDat: Date;

  constructor(interest_type_id: number, type_name: string, description: string, creDat: Date) {
    this.interest_type_id = interest_type_id;
    this.type_name = type_name;
    this.description = description;
    this.creDat = creDat;
  }

  public getInterestTypeId(): number {
    return this.interest_type_id;
  }

  public setInterestTypeId(value: number): void {
    this.interest_type_id = value;
  }

  public getTypeName(): string {
    return this.type_name;
  }

  public setTypeName(value: string): void {
    this.type_name = value;
  }

  public getDescription(): string {
    return this.description;
  }

  public setDescription(value: string): void {
    this.description = value;
  }

  public getCreDat(): Date {
    return this.creDat;
  }

  public setCreDat(value: Date): void {
    this.creDat = value;
  }
}
