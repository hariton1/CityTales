import {UUID} from 'node:crypto';

export interface QuizUserDto {
  id: number;
  quiz: number;
  player: UUID;
  createdAt: Date;
}
