import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnboardingSignUpComponent } from './onboarding-sign-up.component';

describe('OnboardingSignUpComponent', () => {
  let component: OnboardingSignUpComponent;
  let fixture: ComponentFixture<OnboardingSignUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OnboardingSignUpComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OnboardingSignUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
