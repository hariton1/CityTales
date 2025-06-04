import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricPersonDetailComponent } from './historic-person-detail.component';

describe('HistoricPersonDetailComponent', () => {
  let component: HistoricPersonDetailComponent;
  let fixture: ComponentFixture<HistoricPersonDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricPersonDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoricPersonDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
