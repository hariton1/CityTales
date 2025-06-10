import {UUID} from 'node:crypto';
import {Question} from './question.dto';

export interface Quiz {
  id: number;
  name: string;
  description: string;
  category: string;
  creator: UUID;
  questions: Question[];
  createdAt: Date;
}
