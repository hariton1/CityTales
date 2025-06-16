import {computed, inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SERVER_ADDRESS} from '../globals';
import {UUID} from 'node:crypto';
import {Quiz} from '../dto/quiz.dto';
import {TuiAlertService} from '@taiga-ui/core';
import {QuizResult} from '../dto/quiz.result.dto';

@Injectable({providedIn: 'root'})
export class QuizService {
  private httpClient = inject(HttpClient);
  private PATH = SERVER_ADDRESS + 'quizzes/';
  private quizzesSignal = signal<Quiz[]>([]);
  private quizzesResultsSignal = signal<QuizResult[]>([]);
  quizzes = computed(() => this.quizzesSignal());
  quizzesResults = computed(() => this.quizzesResultsSignal());
  private readonly alerts = inject(TuiAlertService);

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
      console.log('QUIZZES RESULTS: ', data);
    });
  }
}
