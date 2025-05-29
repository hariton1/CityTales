import {Component, OnInit} from '@angular/core';
import {
  TuiButton,
  TuiHint,
  TuiIcon,
  TuiLabel,
  TuiTextfieldComponent,
  TuiTextfieldDirective,
} from '@taiga-ui/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TuiSlider} from '@taiga-ui/kit';
import {AsyncPipe, PercentPipe} from '@angular/common';
import {TuiInputDateModule, TuiInputModule} from '@taiga-ui/legacy';
import {BehaviorSubject, distinctUntilChanged, forkJoin, map, Observable, of, switchMap, timer} from 'rxjs';
import {TUI_FALSE_HANDLER, tuiClamp, tuiRound} from '@taiga-ui/cdk';
import {FeedbackDto} from '../../../user_db.dto/feedback.dto';
import {FeedbackService} from '../../../user_db.services/feedback.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-feedback',
  imports: [
    TuiButton,
    TuiTextfieldComponent,
    TuiLabel,
    ReactiveFormsModule,
    TuiTextfieldDirective,
    AsyncPipe,
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
export class FeedbackComponent implements OnInit {

  protected min = 0;
  protected max = 100;
  protected value = 100;

  wikiId: string | null = null;

  constructor(private feeedbackService: FeedbackService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    // Method 1: Snapshot (simpler but doesn't react to parameter changes)
    this.wikiId = this.route.snapshot.queryParamMap.get('wikiId');
    console.log(`Processing feedback for wikiId: ${this.wikiId}`);
  }


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
    console.log(this.form.value.fb_content);
    let newFeedbackDto = new FeedbackDto(
      -1,
      'f5599c8c-166b-495c-accc-65addfaa572b',
      1,
      this.value,
      this.form.value.fb_content ?? '',
      new Date()
    );
    console.log(newFeedbackDto.toString());

    const createRequests: Observable<any>[] = [];

    createRequests.push(this.feeedbackService.createNewFeedback(newFeedbackDto));

    if (createRequests.length > 0) {
      forkJoin(createRequests).subscribe({
        next: (results) => {
          console.log('All feedbacks created successfully', results);
        },
        error: (err) => {
          console.error('Error creating feedback:', err);
        }
      });
    } else {
      console.log('No new feedback to create');
    }
  }

  protected readonly tuiRound = tuiRound;

  protected form = new FormGroup({
    fb_content: new FormControl(''),
  });
}
