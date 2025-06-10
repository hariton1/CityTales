import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameQuizComponent } from './game-quiz.component';

describe('GameQuizComponent', () => {
  let component: GameQuizComponent;
  let fixture: ComponentFixture<GameQuizComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameQuizComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameQuizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
