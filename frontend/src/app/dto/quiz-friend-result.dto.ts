import {UUID} from 'node:crypto';

export interface QuizFriendResult {
  quiz: number;
  friend: UUID;
  correctnessPercentage: number;
  questionsAnswered: number;
}
