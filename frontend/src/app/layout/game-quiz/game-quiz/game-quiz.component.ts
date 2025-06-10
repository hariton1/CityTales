import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {TuiAppearance, TuiButton, TuiScrollbar, TuiTitle} from '@taiga-ui/core';
import {TuiBadge} from '@taiga-ui/kit';
import {TuiCardLarge, TuiHeader} from '@taiga-ui/layout';
import {NgForOf} from '@angular/common';
import {QuizService} from '../../../services/quiz.service';
import {UUID} from 'node:crypto';

@Component({
  selector: 'app-game-quiz',
  imports: [
    TuiTitle,
    TuiScrollbar,
    TuiBadge,
    TuiButton,
    TuiAppearance,
    TuiHeader,
    TuiCardLarge,
    NgForOf
  ],
  templateUrl: './game-quiz.component.html',
  styleUrl: './game-quiz.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameQuizComponent {

  private readonly quizService = inject(QuizService);
  creatorId: UUID = '' as UUID;
  users: UUID[] = [];
  newQuiz = this.quizService.newQuiz;

  generateNewQuiz(): void {
    let category = 'Tour';
    this.retrieveUserID();
    this.users.push(this.creatorId);
    this.quizService.generateNewQuiz(category, this.users);
  }

  protected readonly userQuizzes = [
    {
      header: 'Quiz 1',
      description: 'on tours'
    },
    {
      header: 'Quiz 2',
      description: 'on interests'
    },
    {
      header: 'Quiz 3',
      description: 'on tours'
    },
    {
      header: 'Quiz 4',
      description: 'on random'
    },
    {
      header: 'Quiz 5',
      description: 'on random'
    },
    {
      header: 'Quiz 6',
      description: 'on random'
    },
    {
      header: 'Quiz 7',
      description: 'on random'
    },
  ];

  protected readonly sharedQuizzes = this.userQuizzes;

  retrieveUserID() {
    const stored = localStorage.getItem("user_uuid") as UUID;
    if (stored) {
      this.creatorId = stored;
    }
  }

}
