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
import {NgForOf, NgIf, NgOptimizedImage} from '@angular/common';
import {QuizService} from '../../../services/quiz.service';
import {UUID} from 'node:crypto';
import {TuiResponsiveDialogOptions} from '@taiga-ui/addon-mobile';
import {Quiz} from '../../../dto/quiz.dto';

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
    NgOptimizedImage,
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
  quizzes = this.quizService.quizzes;
  chosenCategory = '';
  playingQuiz: Quiz | undefined;
  answers: string[] = [];
  image: string = '';


  protected dropdownOpen = false;

  ngOnInit(): void {
    this.retrieveUserID();
    console.log('load quizzes for ', this.creatorId)
    this.quizService.getQuizzesByUserId(this.creatorId);
    console.log('LOAD QUIZZES ON INIT', this.quizzes());
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
    this.playingQuiz = this.quizzes()[index];
    console.log(this.playingQuiz);
    this.options = {
      label: this.playingQuiz.name,
      size: 'l',
    };

    let question = this.playingQuiz.questions.at(0);

    this.answers.push(question!.answer);
    this.answers.push(question!.wrongAnswerA);
    this.answers.push(question!.wrongAnswerB);
    this.answers.push(question!.wrongAnswerC);
    this.image = question!.image;

    this.openQuizPlay = true;
  }

}
