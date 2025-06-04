import {Component, inject} from '@angular/core';
import {NgForOf} from "@angular/common";
import {TuiAlertService, TuiAutoColorPipe, TuiButton, TuiInitialsPipe, TuiTitle} from "@taiga-ui/core";
import {TUI_CONFIRM, TuiAvatar, TuiConfirmData, TuiStatus} from "@taiga-ui/kit";
import {TuiTableDirective, TuiTableTbody, TuiTableTd, TuiTableTh} from "@taiga-ui/addon-table";
import {UserService} from '../../../user_db.services/user.service';
import {FeedbackService} from '../../../user_db.services/feedback.service';
import {LocationService} from '../../../services/location.service';
import {TuiCell} from '@taiga-ui/layout';
import {catchError, forkJoin, of, switchMap} from 'rxjs';
import {TuiResponsiveDialogService} from '@taiga-ui/addon-mobile';

@Component({
  selector: 'app-feedback-list',
  imports: [
    NgForOf,
    TuiAutoColorPipe,
    TuiAvatar,
    TuiButton,
    TuiInitialsPipe,
    TuiStatus,
    TuiTableDirective,
    TuiTableTbody,
    TuiTableTd,
    TuiTableTh,
    TuiTitle,
    TuiCell
  ],
  templateUrl: './feedback-list.component.html',
  styleUrl: './feedback-list.component.scss'
})
export class FeedbackListComponent {

  protected feedbacks: any[];

  constructor(private userService: UserService,
              private feedbackService: FeedbackService,
              private locationService: LocationService ) {
    this.feedbacks = [];
  }

  private dialogs = inject(TuiResponsiveDialogService);
  private alerts = inject(TuiAlertService);

  ngOnInit(): void {
    this.feedbackService.getAllFeedbacks().subscribe({
      next: (feedbacks) => {
        // Process each feedback independently
        feedbacks.forEach(feedback => {
          // You can process each feedback here
          console.log('Processing feedback:', feedback);
          const locationObs = this.locationService.getLocationByVHWId(feedback.getArticleId())
            .pipe(catchError(error => {
              console.error('Error fetching location:', error);
              return of(null);
            }));

          const userObs = this.userService.getUserById(feedback.getUserId())
            .pipe(catchError(error => {
              console.error('Error fetching user:', error);
              return of(null);
            }));

          forkJoin([locationObs, userObs]).subscribe(([article, user]) => {
            const articleName = (<any>article).name || 'Unknown';
            const userName = user?.email || 'Unknown';

            this.feedbacks.push({
              feedback_id: feedback.getFeedbackId(),
              article_id: feedback.getArticleId(),
              article_name: articleName,
              user_id: feedback.getUserId(),
              user_name: userName,
              fb_content: feedback.getFbContent(),
              rating: feedback.getRating()
            });
          });

        });
      },
      error: (error) => {
        console.error('Error fetching feedbacks:', error);
      }
    });

  }

  protected handleApproveFeedback(feedback: any): void {
    // Use getters to ensure we get the values correctly
    console.log(feedback);
    this.confirmApprove(feedback.feedback_id);
    /*this.feedbackService.approveFeedback(feedback.feedback_id).subscribe({
      next: (results) => {
        console.log('works', results);
      },
      error: (err) => {
        console.error('no works', err);
      }
    });*/

  }

  protected handleDeleteFeedback(feedback: any): void {
    // Use getters to ensure we get the values correctly
    console.log(feedback);
    this.confirmDelete(feedback.feedback_id);
    /*this.feedbackService.deleteFeedback(feedback.feedback_id).subscribe({
      next: (results) => {
        console.log('works', results);
      },
      error: (err) => {
        console.error('no works', err);
      }
    });*/
  }

  protected confirmApprove(feedbackId: number): void {
    console.log(feedbackId);
    const data: TuiConfirmData = {
      content:
        'Feedback will be approved. This action cannot be undone!',
      yes: 'Yes',
      no: 'No',
    };

    this.dialogs
      .open<boolean>(TUI_CONFIRM, {
        label: 'Are you sure you want to approve the feedback?',
        size: 'm',
        data,
      })
      .pipe(
        switchMap((response) => {
          if (response) {
            this.feedbackService.approveFeedback(feedbackId).subscribe({
              next: (results) => {
                console.log('works', results);
                return this.alerts.open('Feedback approved successfully!', {label: 'Success!', appearance: 'success', autoClose: 3000});
              },
              error: (err) => {
                console.error('no works', err);
              }
            });
          }
          return this.alerts.open('Feedback approval cancelled!', {label: 'Cancelled!', appearance: 'warning', autoClose: 3000});
        }))
      .subscribe();
  }

  protected confirmDelete(feedbackId: number): void {
    console.log(feedbackId);
    const data: TuiConfirmData = {
      content:
        'Feedback will be deleted. This action cannot be undone!',
      yes: 'Yes',
      no: 'No',
    };

    this.dialogs
      .open<boolean>(TUI_CONFIRM, {
        label: 'Are you sure you want to delete the feedback?',
        size: 'm',
        data,
      })
      .pipe(
        switchMap((response) => {
          if (response) {
            this.feedbackService.deleteFeedback(feedbackId).subscribe({
              next: (results) => {
                console.log('works', results);
                return this.alerts.open('Feedback deleted successfully!', {label: 'Success!', appearance: 'success', autoClose: 3000});
              },
              error: (err) => {
                console.error('no works', err);
              }
            });
          }
          return this.alerts.open('Feedback deletion cancelled!', {label: 'Cancelled!', appearance: 'warning', autoClose: 3000});
        }))
      .subscribe();
  }
}
