import { ComponentFixture, TestBed } from '@angular/core/testing';

import { XpTrackerComponent } from './xp-tracker.component';

describe('XpTrackerComponent', () => {
  let component: XpTrackerComponent;
  let fixture: ComponentFixture<XpTrackerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [XpTrackerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(XpTrackerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
