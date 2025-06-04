import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricEventDetailComponent } from './historic-event-detail.component';

describe('HistoricEventDetailComponent', () => {
  let component: HistoricEventDetailComponent;
  let fixture: ComponentFixture<HistoricEventDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricEventDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoricEventDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
