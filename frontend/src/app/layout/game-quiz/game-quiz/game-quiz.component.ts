import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {
  TuiButton, TuiDataList,
  TuiDropdown,
  TuiHint, TuiSizeL, TuiSizeS,
  TuiSurface,
  TuiTitle
} from '@taiga-ui/core';
import {TuiAvatar, TuiDataListDropdownManager} from '@taiga-ui/kit';
import {TuiCardLarge, TuiHeader} from '@taiga-ui/layout';
import {NgForOf, NgIf} from '@angular/common';
import {QuizService} from '../../../services/quiz.service';
import {UUID} from 'node:crypto';

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
    TuiDataListDropdownManager
  ],
  templateUrl: './game-quiz.component.html',
  styleUrl: './game-quiz.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameQuizComponent implements OnInit {

  private readonly quizService = inject(QuizService);
  protected size: TuiSizeL | TuiSizeS = 's';
  protected open = false;
  creatorId: UUID = '' as UUID;
  users: UUID[] = [];
  newQuiz = this.quizService.newQuiz;
  quizzes = this.quizService.quizzes;
  chosenCategory = '';

  protected dropdownOpen = false;

  ngOnInit(): void {
    this.retrieveUserID();
    this.quizService.getQuizzesByUserId(this.creatorId);
    console.log(this.quizzes());
  }

  generateQuiz(category: string) {
    this.open = false;
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
    console.log(index);
  }

}
