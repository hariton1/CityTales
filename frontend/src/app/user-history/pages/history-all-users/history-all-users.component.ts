import { Component } from '@angular/core';
import {DatePipe, NgForOf} from "@angular/common";
import {TuiTableDirective, TuiTableTbody, TuiTableTd, TuiTableTh} from "@taiga-ui/addon-table";
import {TuiTitle} from "@taiga-ui/core";
import {UserHistoriesService} from '../../../user_db.services/user-histories.service';
import {LocationService} from '../../../services/location.service';
import {HelperService} from '../../../user_db.services/helper.service';
import {catchError, forkJoin, of} from 'rxjs';
import {TuiCell} from '@taiga-ui/layout';
import { UserService } from '../../../user_db.services/user.service';

@Component({
  selector: 'app-history-all-users',
  imports: [
    DatePipe,
    NgForOf,
    TuiTableDirective,
    TuiTableTbody,
    TuiTableTd,
    TuiTableTh,
    TuiTitle,
    TuiCell
  ],
  templateUrl: './history-all-users.component.html',
  styleUrl: './history-all-users.component.scss'
})
export class HistoryAllUsersComponent {

  protected userHistories: any[];

  constructor(private userHistoriesService: UserHistoriesService,
              private locationService: LocationService,
              private helperService: HelperService,
              private userService: UserService) {
    this.userHistories = [];
  }

  ngOnInit(): void {
    this.userHistoriesService.getAllUserHistories().subscribe({
      next: (userHistories) => {
        // Process each user history independently
        userHistories.forEach(userHistory => {
          // You can process each user history here
          console.log('Processing user history:', userHistory);
          const locationObs = this.locationService.getLocationByVHWId(userHistory.getArticleId())
            .pipe(catchError(error => {
              console.error('Error fetching location:', error);
              return of(null);
            }));

          const userObs = this.userService.getUserById(userHistory.getUserId())
            .pipe(catchError(error => {
              console.error('Error fetching user:', error);
              return of(null);
            }));

          forkJoin([locationObs, userObs]).subscribe(([article, user]) => {
            const articleName = (<any>article).name || 'Unknown';
            const userName = user?.email || 'Unknown';

            this.userHistories.push({
              article_name: articleName,
              username: userName,
              open_dt: this.helperService.sanitizeDate(userHistory.getOpenDt())
            });
          });

        });
      },
      error: (error) => {
        console.error('Error fetching user histories:', error);
      }
    });
  }

}
