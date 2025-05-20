import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricPlaceDetailComponent } from './historic-place-detail.component';

describe('HistoricPlaceDetailComponent', () => {
  let component: HistoricPlaceDetailComponent;
  let fixture: ComponentFixture<HistoricPlaceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricPlaceDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoricPlaceDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
