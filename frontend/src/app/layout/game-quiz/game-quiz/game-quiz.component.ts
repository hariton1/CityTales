import {ChangeDetectionStrategy, Component, computed, inject, OnInit, signal} from '@angular/core';
import {
  TuiAlertService,
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
import {Quiz} from '../../../dto/quiz.dto';
import {Question} from '../../../dto/question.dto';
import {UserService} from '../../../services/user.service';

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
  private readonly userService = inject(UserService);
  private readonly alerts = inject(TuiAlertService);
  protected options: Partial<TuiResponsiveDialogOptions> = {};
  protected openQuizPlay = false;
  protected value = '';
  protected size: TuiSizeL | TuiSizeS = 's';
  protected openGenerateMenu = false;
  creatorId: UUID = '' as UUID;
  users: UUID[] = [];
  quizzes = this.quizService.quizzes;
  chosenCategory = '';
  userName = '';
  playingQuiz: Quiz | undefined;
  numberOfQuestions = 0;
  currentQuizIndex = 0;
  currentQuestionIndex = 0;
  correctAnswer = '';
  score = 0;
  toggleScoreEditable = false;

  currentQuestionSignal = signal<string>('');
  currentAnswer1Signal = signal<string>('');
  currentAnswer2Signal = signal<string>('');
  currentAnswer3Signal = signal<string>('');
  currentAnswer4Signal = signal<string>('');
  currentImageSignal = signal<string>('');
  currentQuestion = computed(() => this.currentQuestionSignal());
  currentAnswer1 = computed(() => this.currentAnswer1Signal());
  currentAnswer2 = computed(() => this.currentAnswer2Signal());
  currentAnswer3 = computed(() => this.currentAnswer3Signal());
  currentAnswer4 = computed(() => this.currentAnswer4Signal());
  currentImage = computed(() => this.currentImageSignal());

  protected dropdownOpen = false;

  ngOnInit(): void {
    this.retrieveUserID();
    this.quizService.getQuizzesByUserId(this.creatorId);
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
    this.userService.getUserWithRoleById(this.creatorId).subscribe((user) => {
      this.userName = user.email.substring(0, user.email.indexOf('@'));
    });
  }

  playQuiz(index: number): void {
    this.score = 0;
    this.currentQuestionIndex = 0;
    this.currentQuizIndex = index;
    this.playingQuiz = this.quizzes()[index];
    this.numberOfQuestions = this.quizzes()[this.currentQuizIndex].questions.length;
    console.log(this.playingQuiz);
    this.options = {
      label: this.playingQuiz.name,
      size: 'l',
    };

    this.loadNextQuestion()

    this.openQuizPlay = true;
  }

  loadNextQuestion(): void {
    this.toggleScoreEditable = true;
    if (this.currentQuestionIndex < this.numberOfQuestions) {
      this.currentQuestionIndex++;
    } else {
      this.openQuizPlay = false;
      this.alerts.open(`Good job!`, {
        label: 'Final score: ${this.score}',
        appearance: 'primary',
        autoClose: 5000
      }).subscribe()
      // todo persist score
    }

    let question = this.quizzes()[this.currentQuizIndex].questions[this.currentQuestionIndex];
    this.correctAnswer = question.answer;

    //todo mix which gets assigned where
    this.currentQuestionSignal.set(question.question);
    this.currentAnswer2Signal.set(question.answer);
    this.currentAnswer1Signal.set(question.wrong_answer_a);
    this.currentAnswer3Signal.set(question.wrong_answer_b);
    this.currentAnswer4Signal.set(question.wrong_answer_c);
    this.currentImageSignal.set(question.image);

    this.openQuizPlay = true;
  }

  check(answer: string) {
    if (answer === this.correctAnswer) {
      if (this.toggleScoreEditable) {
        this.score++;
        this.toggleScoreEditable = false;
      }
      this.alerts.open('Good Job!', {
        label: 'Correct!',
        appearance: 'positive',
        autoClose: 1000
      }).subscribe()
    } else {
      this.toggleScoreEditable = false;
      this.alerts.open('Too bad!', {
        label: 'Wrong!',
        appearance: 'negative',
        autoClose: 1000
      }).subscribe()
    }
  }
}
