import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TourLayoutComponent } from './tour-layout-component';

describe('TourLayoutComponent', () => {
  let component: TourLayoutComponent;
  let fixture: ComponentFixture<TourLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourLayoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TourLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
