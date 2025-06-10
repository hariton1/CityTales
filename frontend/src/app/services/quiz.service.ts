import {computed, inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SERVER_ADDRESS} from '../globals';
import {UUID} from 'node:crypto';
import {Quiz} from '../dto/quiz.dto';

@Injectable({ providedIn: 'root' })
export class QuizService {
  private httpClient = inject(HttpClient);
  private PATH = SERVER_ADDRESS + 'quizzes/';
  private newQuizSignal = signal<Quiz>(this.getNewQuizDefaultData());
  newQuiz = computed(() => this.newQuizSignal());


  generateNewQuiz(category: string, userIds : UUID[]) {
    this.httpClient.post<Quiz>(this.PATH + 'quiz/create/' + category, userIds).subscribe((data) => {
      console.log(data);
      this.newQuizSignal.set(data);
    });
  }

  getNewQuizDefaultData() {
    return {id: 0, name: 'new quiz', description: 'default', category: 'Tour', creator: '' as UUID, questions: [], createdAt: new Date()};
  }
}
