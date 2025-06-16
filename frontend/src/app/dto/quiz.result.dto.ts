import {QuestionResults} from './question-results.dto';
import {QuizFriendResult} from './quiz-friend-result.dto';

export interface QuizResult {
  quiz: number | undefined;
  questionResults: QuestionResults[];
  friendResults: QuizFriendResult[];
}
