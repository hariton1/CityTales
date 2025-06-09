import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoryAllUsersComponent } from './history-all-users.component';

describe('HistoryAllUsersComponent', () => {
  let component: HistoryAllUsersComponent;
  let fixture: ComponentFixture<HistoryAllUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryAllUsersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoryAllUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
