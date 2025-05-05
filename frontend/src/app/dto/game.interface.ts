export interface GameInterface {
  getId(): number;
  getName(): string;
  getQuestion(): string;
  getImage(): string;
  getOptions(): string[];
  getAnswer(): string;
}
