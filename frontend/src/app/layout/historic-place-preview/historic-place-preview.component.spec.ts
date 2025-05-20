import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricPlacePreviewComponent } from './historic-place-preview.component';

describe('HistoricPlacePreviewComponent', () => {
  let component: HistoricPlacePreviewComponent;
  let fixture: ComponentFixture<HistoricPlacePreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricPlacePreviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoricPlacePreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
