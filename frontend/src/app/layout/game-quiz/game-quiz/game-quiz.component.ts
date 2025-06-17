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
import {QuizResult} from '../../../dto/quiz.result.dto';
import {QuestionResults} from '../../../dto/question-results.dto';
import {UserPointsService} from '../../../user_db.services/user-points.service';
import {UserPointDto} from '../../../user_db.dto/user-point.dto';
import {tuiRound} from '@taiga-ui/cdk';
import {FriendsService} from '../../../user_db.services/friends.service';
import {FriendsDto} from '../../../user_db.dto/friends.dto';
import {QuizUserDto} from '../../../dto/quiz-user.dto';

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
  private readonly userPointsService = inject(UserPointsService);
  private readonly friendsService = inject(FriendsService);
  private readonly alerts = inject(TuiAlertService);
  protected options: Partial<TuiResponsiveDialogOptions> = {};
  protected openQuizPlay = false;
  protected value = '';
  protected size: TuiSizeL | TuiSizeS = 's';
  protected openGenerateMenu = false;
  creatorId: UUID = '' as UUID;
  friends: FriendsDto[] = [];
  users: UUID[] = [];
  quizzes = this.quizService.quizzes;
  quizzesResults = this.quizService.quizzesResults;
  quizCreatorNames = this.quizService.quizCreatorNames;
  friendScores = this.quizService.friendScores;
  questionResults: QuestionResults[] = [];
  chosenCategory = '';
  playingQuiz: Quiz | undefined;
  numberOfQuestions = 0;
  currentQuizIndex = 0;
  currentQuestionIndex = 0;
  correctAnswer = '';
  score = 0;
  toggleScoreEditable = false;
  inviteSent = this.quizService.inviteSuccessfullySent;

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
    this.quizService.getQuizResultsByUserId(this.creatorId);
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
  }

  saveQuizResult(result: QuizResult) {
    this.quizService.saveQuizResult(result);
  }

  playQuiz(index: number): void {
    this.score = 0;
    this.currentQuestionIndex = 0;
    this.currentQuizIndex = index;
    this.playingQuiz = this.quizzes()[index];
    this.numberOfQuestions = this.playingQuiz.questions.length;
    this.options = {
      label: this.playingQuiz.name,
      size: 'l',
    };

    this.loadNextQuestion()

    this.openQuizPlay = true;
  }

  loadNextQuestion(): void {
    this.toggleScoreEditable = true;
    if (this.currentQuestionIndex >= this.numberOfQuestions) {
      this.openQuizPlay = false;
      this.alerts.open(`Good job!`, {
        label: `Final score: ${this.score}`,
        appearance: 'primary',
        autoClose: 5000
      }).subscribe()
      let quiz = this.quizzes()[this.currentQuizIndex];
      let oldScore = this.calcOldScore(this.playingQuiz!.id);
      if (oldScore) {
        let subtractedPointDTO = new UserPointDto(-1, this.creatorId, -oldScore * 5, new Date(), Number(quiz))
        this.userPointsService.createNewPoints(subtractedPointDTO).subscribe();
      }
      let pointDTO = new UserPointDto(-1, this.creatorId, this.score * 5, new Date(), Number(quiz))
      this.userPointsService.createNewPoints(pointDTO).subscribe(data => {
        console.log("Quiz points created")
      });
      this.saveQuizResult({quiz: this.playingQuiz?.id, questionResults: this.questionResults, friendResults: []});
    }

    let question = this.quizzes()[this.currentQuizIndex].questions[this.currentQuestionIndex];
    this.correctAnswer = question.answer;

    this.currentQuestionSignal.set(question.question);
    const shuffledAnswers = [question.answer, question.wrong_answer_a, question.wrong_answer_b, question.wrong_answer_c]
      .sort(() => Math.random() - 0.5);

    [this.currentAnswer1Signal, this.currentAnswer2Signal, this.currentAnswer3Signal, this.currentAnswer4Signal]
      .forEach((signal, index) => signal.set(shuffledAnswers[index]));
    this.currentImageSignal.set(question.image);

    this.openQuizPlay = true;
    this.currentQuestionIndex++;
  }

  check(answer: string) {
    if (answer === this.correctAnswer) {
      if (this.toggleScoreEditable) {
        this.score++;
        this.questionResults.push(this.getNewQuestionResult(true));
        this.toggleScoreEditable = false;
      }
      this.alerts.open('Good Job!', {
        label: 'Correct!',
        appearance: 'positive',
        autoClose: 1000
      }).subscribe()
    } else {
      if (this.toggleScoreEditable) {
        this.questionResults.push(this.getNewQuestionResult(false));
        this.toggleScoreEditable = false;
      }
      this.alerts.open('Too bad!', {
        label: 'Wrong!',
        appearance: 'negative',
        autoClose: 1000
      }).subscribe()
    }
  }

  getNewQuestionResult(correct: boolean): QuestionResults {
    return {
      id: 0,
      question: this.quizzes()[this.currentQuizIndex].questions[this.currentQuestionIndex - 1].id,
      player: this.creatorId,
      correct: correct,
      createdAt: new Date()
    }
  }

  printScore(index: number) {
    let quizId = this.quizzes()[index].id;
    let numOfQuestions = this.quizzes()[index].questions.length;
    let oldScore = this.calcOldScore(quizId)
    if (oldScore !== null) {
      let percentage = tuiRound(oldScore / numOfQuestions * 100, 0);
      return 'Score: ' + percentage + '%';
    }

    return 'Not played yet!';
  }

  calcOldScore(quizId: number) {
    let score = 0;
    let quizResult = this.quizzesResults().find(result => result.quiz === quizId);
    if (quizResult) {
      quizResult?.questionResults.forEach((result) => {
        if (result.correct) {
          score++;
        }
      })
      return score;
    }
    return null;
  }

  retrieveFriends() {
    this.friendsService.getFriendsByFriendOne(this.creatorId).subscribe({
        next: result => {
          result.forEach(result => {
            this.friends.push(result);
          });
        }
      }
    );
    if (!this.inviteSent) {
      let quizUsers: QuizUserDto[] = [];
      this.friends.forEach((friend) => {
        quizUsers.push({ id: 0, quiz: this.playingQuiz!.id, player: friend.friend_two, createdAt: new Date });
      });
      this.quizService.inviteFriendsToQuiz(quizUsers);
      this.alerts.open(`Your friends can now play this quiz too!`, {
        label: `Invite Sent!`,
        appearance: 'success',
        autoClose: 5000
      }).subscribe()
    } else {
      this.alerts.open(`Your friends have already received this quiz!`, {
        label: `Invite already sent!`,
        appearance: 'neutral',
        autoClose: 3000
      }).subscribe()
    }

  }
}
