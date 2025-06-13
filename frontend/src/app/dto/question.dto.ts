export interface Question {
  id: number;
  quiz: number;
  question: string;
  answer: string;
  wrong_answer_a: string;
  wrong_answer_b: string;
  wrong_answer_c: string;
  image: string;
  createdAt: Date;
}
