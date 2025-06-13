import {computed, inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SERVER_ADDRESS} from '../globals';
import {UUID} from 'node:crypto';
import {Quiz} from '../dto/quiz.dto';

@Injectable({ providedIn: 'root' })
export class QuizService {
  private httpClient = inject(HttpClient);
  private PATH = SERVER_ADDRESS + 'quizzes/';
  private newQuizSignal = signal<Quiz>(this.getNewQuizDefaultData('', '' as UUID));
  private quizzesSignal = signal<Quiz[]>([]);
  newQuiz = computed(() => this.newQuizSignal());
  quizzes = computed(() => this.quizzesSignal());

  generateNewQuiz(category: string, userIds : UUID[]) {
    this.httpClient.post<Quiz>(this.PATH + 'quiz/create/' + category, userIds).subscribe((newQuiz) => {
      const currentQuizzes = this.quizzes();
      this.quizzesSignal.set([...currentQuizzes, newQuiz]);
    });
  }

  getQuizzesByUserId(userId : UUID) {
    this.httpClient.get<Quiz[]>(this.PATH + `quiz/user=${userId}`).subscribe((data) => {
      this.quizzesSignal.set(data);
    });
  }

  getNewQuizDefaultData(category: string, creator: UUID) {
    return {id: 0, name: 'new quiz', description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna', category: category, creator: creator, questions: [], createdAt: new Date()};
  }
}
