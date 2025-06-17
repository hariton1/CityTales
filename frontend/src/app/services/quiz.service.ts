import {computed, inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SERVER_ADDRESS} from '../globals';
import {UUID} from 'node:crypto';
import {Quiz} from '../dto/quiz.dto';
import {TuiAlertService} from '@taiga-ui/core';
import {QuizResult} from '../dto/quiz.result.dto';
import {QuizUserDto} from '../dto/quiz-user.dto';
import {UserService} from './user.service';
import {tuiRound} from '@taiga-ui/cdk';

@Injectable({providedIn: 'root'})
export class QuizService {
  private httpClient = inject(HttpClient);
  private readonly userService = inject(UserService);
  private PATH = SERVER_ADDRESS + 'quizzes/';
  private quizzesSignal = signal<Quiz[]>([]);
  private quizzesResultsSignal = signal<QuizResult[]>([]);
  private quizCreatorNamesSignal = signal<string[]>([]);
  private friendScoresSignal = signal<string[]>([]);
  quizzes = computed(() => this.quizzesSignal());
  quizzesResults = computed(() => this.quizzesResultsSignal());
  quizCreatorNames = computed(() => this.quizCreatorNamesSignal());
  friendScores = computed(() => this.friendScoresSignal());
  private readonly alerts = inject(TuiAlertService);
  inviteSuccessfullySent = false;

  generateNewQuiz(category: string, userIds: UUID[]) {
    this.httpClient.post<Quiz>(this.PATH + 'quiz/create/' + category, userIds).subscribe({
      next: (response) => {
        const currentQuizzes = this.quizzes();
        this.quizzesSignal.set([...currentQuizzes, response]);
      },
      error: (err) => {
        if (err.status === 404) {
          this.alerts.open('You don\'t have any tours yet! Create one in the Tours tab!', {
            label: 'No Tours Yet!',
            appearance: 'neutral',
            autoClose: 5000
          }).subscribe()
        }
      }
    });
  }

  getQuizzesByUserId(userId: UUID) {
    this.httpClient.get<Quiz[]>(this.PATH + `quiz/user=${userId}`).subscribe((data) => {
      this.quizzesSignal.set(data);
      this.quizzes().forEach((quiz) => {
        this.userService.getUserWithRoleById(quiz.creator).subscribe((user) => {
          const namesSoFar = this.quizCreatorNames();
          this.quizCreatorNamesSignal.set([... namesSoFar, (user.email.substring(0, user.email.indexOf('@')))]);
        });
      })
    });
  }

  saveQuizResult(quiz: QuizResult) {
    this.httpClient.post<QuizResult>(this.PATH + `result/save`, quiz).subscribe((data) => {
      const currentResults = this.quizzesResults();
      const entryToUpdate = currentResults.find(result => result.quiz === data.quiz);
      if (entryToUpdate) {
        currentResults.splice(currentResults.indexOf(entryToUpdate), 1);
      }
      this.quizzesResultsSignal.set([... currentResults, data]);
    });
  }

  getQuizResultsByUserId(userId: UUID) {
    this.httpClient.get<QuizResult[]>(this.PATH + `result/user=${userId}`).subscribe((data) => {
      this.quizzesResultsSignal.set(data);
      this.fillFriendScoresData(data);
    });
  }

  inviteFriendsToQuiz(friends: QuizUserDto[]) {
    this.httpClient.post(this.PATH + `quiz/invite`, friends).subscribe({
      next: (response) => {this.inviteSuccessfullySent = true;},
      error: (err) => {console.log(err)}
    });
  }

  fillFriendScoresData(quizzes: QuizResult[]) {
    quizzes.forEach((quiz) => {
      const currentFriendScores = this.friendScores();
      let friendsScore = '';
      quiz.friendResults.forEach((friend) => {
        this.userService.getUserWithRoleById(friend.friend).subscribe((user) => {
          let name = user.email.substring(0, user.email.indexOf('@'));

          let percentage = friend.correctness_percentage * 100;
          let score = tuiRound(percentage,0);
          let friendString = name + ': ' + score + '%\n';
          friendsScore += friendString;
          this.friendScoresSignal.set([... currentFriendScores, friendsScore]);
        });
      })
    });
  }
}
