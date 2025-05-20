import { Component } from '@angular/core';
import {
  TuiButton,
  TuiError, TuiHint,
  TuiIcon,
  TuiLabel,
  TuiTextfieldComponent,
  TuiTextfieldDirective,
  TuiTitle
} from '@taiga-ui/core';
import {TuiForm, TuiHeader} from '@taiga-ui/layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TuiFieldErrorPipe, TuiPassword, TuiSlider, TuiTooltip} from '@taiga-ui/kit';
import {AsyncPipe, PercentPipe} from '@angular/common';
import {TuiInputDateModule, TuiInputModule} from '@taiga-ui/legacy';
import {BehaviorSubject, distinctUntilChanged, map, of, switchMap, timer} from 'rxjs';
import {TUI_FALSE_HANDLER, tuiClamp, tuiRound} from '@taiga-ui/cdk';

@Component({
  selector: 'app-feedback',
  imports: [
    TuiButton,
    TuiForm,
    TuiTextfieldComponent,
    TuiHeader,
    TuiTitle,
    TuiLabel,
    ReactiveFormsModule,
    TuiTextfieldDirective,
    TuiError,
    TuiFieldErrorPipe,
    AsyncPipe,
    TuiPassword,
    TuiTooltip,
    TuiIcon,
    TuiInputModule,
    TuiInputDateModule,
    FormsModule,
    PercentPipe,
    TuiHint,
    TuiSlider
  ],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.scss'
})
export class FeedbackComponent {

  protected min = 0;
  protected max = 100;
  protected value = 100;

  protected readonly active$ = new BehaviorSubject(false);
  protected readonly showHint$ = this.active$.pipe(
    distinctUntilChanged(),
    switchMap((active) =>
      active ? of(true) : timer(1000).pipe(map(TUI_FALSE_HANDLER)),
    ),
  );

  protected onKeydown(show: boolean): void {
    this.active$.next(show);
  }

  protected change(step: number): void {
    this.value = tuiRound(tuiClamp(this.value + step, this.min, this.max),0);
  }

  onSubmit(): void {
    this.value = tuiRound(this.value,0);
    console.log(this.value);
  }

  protected readonly tuiRound = tuiRound;
}
