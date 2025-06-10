import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoryOneUserComponent } from './history-one-user.component';

describe('HistoryOneUserComponent', () => {
  let component: HistoryOneUserComponent;
  let fixture: ComponentFixture<HistoryOneUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryOneUserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoryOneUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
