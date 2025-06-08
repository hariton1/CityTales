import {ChangeDetectionStrategy, Component} from '@angular/core';
import {TuiAppearance, TuiButton, TuiScrollbar, TuiTitle} from '@taiga-ui/core';
import {TuiBadge} from '@taiga-ui/kit';
import {TuiCardLarge, TuiHeader} from '@taiga-ui/layout';
import {NgForOf} from '@angular/common';

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
}
