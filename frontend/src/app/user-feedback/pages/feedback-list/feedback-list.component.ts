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
    this.feedbackService.getAllFeedbacks().subscribe(
      (feedbacks) => {
        this.feedbacks = feedbacks;
      }
    );
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
