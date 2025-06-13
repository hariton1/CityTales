import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {
  TuiButton, TuiDataList, TuiDialog,
  TuiDropdown,
  TuiHint, TuiSizeL, TuiSizeS,
  TuiSurface,
  TuiTitle
} from '@taiga-ui/core';
import {TuiAvatar, TuiDataListDropdownManager, TuiProgress} from '@taiga-ui/kit';
import {TuiCardLarge, TuiHeader} from '@taiga-ui/layout';
import {NgForOf, NgIf} from '@angular/common';
import {QuizService} from '../../../services/quiz.service';
import {UUID} from 'node:crypto';
import {TuiResponsiveDialogOptions} from '@taiga-ui/addon-mobile';

@Component({
  selector: 'app-game-quiz',
  imports: [
    TuiTitle,
    TuiButton,
    TuiHeader,
    TuiCardLarge,
    NgForOf,
    TuiSurface,
    TuiAvatar,
    TuiHint,
    NgIf,
    TuiDropdown,
    TuiDataList,
    TuiDataListDropdownManager,
    TuiDialog,
    TuiProgress,
  ],
  templateUrl: './game-quiz.component.html',
  styleUrl: './game-quiz.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameQuizComponent implements OnInit {

  private readonly quizService = inject(QuizService);
  protected options: Partial<TuiResponsiveDialogOptions> = {};
  protected openQuizPlay = false;
  protected value = '';
  protected size: TuiSizeL | TuiSizeS = 's';
  protected openGenerateMenu = false;
  creatorId: UUID = '' as UUID;
  users: UUID[] = [];
  newQuiz = this.quizService.newQuiz();
  quizzes = this.quizService.quizzes();
  chosenCategory = '';
  answers: string[] = [];


  protected dropdownOpen = false;

  ngOnInit(): void {
    this.retrieveUserID();
    this.quizService.getQuizzesByUserId(this.creatorId);
    console.log(this.quizzes);
  }

  generateQuiz(category: string) {
    this.openGenerateMenu = false;
    this.chosenCategory = category;
    this.retrieveUserID();
    this.users.push(this.creatorId);
    this.quizService.generateNewQuiz(this.chosenCategory, this.users);
  }

  retrieveUserID() {
    const stored = localStorage.getItem("user_uuid") as UUID;
    if (stored) {
      this.creatorId = stored;
    }
    // todo retrieve user name
  }

  playQuiz(index: number): void {
    let quiz = this.quizzes[index];
    this.options = {
      label: quiz.name,
      size: 'l',
    };

    this.answers.push('1654');
    this.answers.push('1912');
    this.answers.push('1835');
    this.answers.push('1745');

    this.openQuizPlay = true;
  }

}
