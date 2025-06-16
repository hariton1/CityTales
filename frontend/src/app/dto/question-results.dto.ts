import {UUID} from 'node:crypto';

export interface QuestionResults {
  id: number;
  question: number;
  player: UUID;
  correct: boolean;
  createdAt: Date;
}
