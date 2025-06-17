import {UUID} from 'node:crypto';

export interface QuizFriendResult {
  quiz: number;
  friend: UUID;
  correctness_percentage: number;
  questionsAnswered: number;
}
