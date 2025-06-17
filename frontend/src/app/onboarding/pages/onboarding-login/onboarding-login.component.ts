import {NgIf} from '@angular/common';
import {Component, OnInit, signal} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {TuiPulse} from '@taiga-ui/kit';
import {TuiButton, TuiHintDirective, TuiHintManual, TuiTitle} from '@taiga-ui/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {RouterLink} from '@angular/router';


@Component({
  selector: 'app-onboarding',
  imports: [
    ReactiveFormsModule,
    ReactiveFormsModule,
    TuiButton,
    TuiTitle,
    NgIf,
    TuiPulse,
    TuiHintDirective,
    TuiHintManual,
    RouterLink
  ],
  templateUrl: './onboarding-login.component.html',
  styleUrl: './onboarding-login.component.less',
})

export class OnboardingLoginComponent implements OnInit{

  protected readonly step = signal(0);

  protected error: any = null;

  isMobile = false;

  constructor(private breakpointObserver: BreakpointObserver) {
  }

  ngOnInit(): void {
    this.breakpointObserver
      .observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape])
      .subscribe(result => {
        this.isMobile = result.matches;
      });
  }
}
