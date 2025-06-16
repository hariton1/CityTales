import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnboardingLoginComponent } from './onboarding-login.component';

describe('OnboardingComponent', () => {
  let component: OnboardingLoginComponent;
  let fixture: ComponentFixture<OnboardingLoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OnboardingLoginComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OnboardingLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
