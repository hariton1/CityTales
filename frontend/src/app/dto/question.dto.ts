export interface Question {
  id: number;
  quiz: number;
  question: string;
  answer: string;
  wrongAnswerA: string;
  wrongAnswerB: string;
  wrongAnswerC: string;
  image: string;
  createdAt: Date;
}
