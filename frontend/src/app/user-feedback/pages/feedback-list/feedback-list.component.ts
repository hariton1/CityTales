import { Component } from '@angular/core';
import {NgForOf} from "@angular/common";
import {TuiAutoColorPipe, TuiButton, TuiInitialsPipe, TuiTitle} from "@taiga-ui/core";
import {TuiAvatar, TuiStatus} from "@taiga-ui/kit";
import {TuiTableDirective, TuiTableTbody, TuiTableTd, TuiTableTh} from "@taiga-ui/addon-table";
import {UserService} from '../../../user_db.services/user.service';
import {FeedbackService} from '../../../user_db.services/feedback.service';
import {LocationService} from '../../../services/location.service';
import {TuiCell} from '@taiga-ui/layout';

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

  ngOnInit(): void {
    this.feedbackService.getAllFeedbacks().subscribe({
      next: (feedbacks) => {
        // Process each feedback independently
        feedbacks.forEach(feedback => {
          // You can process each feedback here
          console.log('Processing feedback:', feedback);
          let articleName = '';
          let userName = '';
          this.locationService.getLocationByVHWId(feedback.getArticleId()).subscribe(
            (article) => {
              articleName = article.building.name;
            }
          )
          this.userService.getUserById(feedback.getUserId()).subscribe(
            (user) => {
              userName = user.email;
            }
          )
          let tmp = {
            article_id: feedback.getArticleId(),
            article_name: articleName,
            user_id: feedback.getUserId(),
            user_name: userName,
            fb_content: feedback.getFbContent(),
            rating: feedback.getRating()
          }
          // Add the processed feedback to the array
          this.feedbacks.push(tmp);
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
    /*const id = user.id;
    const email = user.email;
    this.confirmDelete(id,email);*/
  }

  protected handleDeleteFeedback(feedback: any): void {
    // Use getters to ensure we get the values correctly
    console.log(feedback);
    /*const id = user.id;
    const email = user.email;
    this.confirmDelete(id,email);*/
  }

}
