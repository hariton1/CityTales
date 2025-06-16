import {computed, inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SERVER_ADDRESS} from '../globals';
import {UUID} from 'node:crypto';
import {Quiz} from '../dto/quiz.dto';
import {TuiAlertService} from '@taiga-ui/core';

@Injectable({providedIn: 'root'})
export class QuizService {
  private httpClient = inject(HttpClient);
  private PATH = SERVER_ADDRESS + 'quizzes/';
  private quizzesSignal = signal<Quiz[]>([]);
  quizzes = computed(() => this.quizzesSignal());
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
}
