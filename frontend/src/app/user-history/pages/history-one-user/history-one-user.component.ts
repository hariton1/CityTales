import { Component } from '@angular/core';
import {DatePipe, NgForOf} from "@angular/common";
import {TuiButton, TuiIcon, TuiTitle} from "@taiga-ui/core";
import {TuiTableDirective, TuiTableTbody, TuiTableTd, TuiTableTh} from "@taiga-ui/addon-table";
import {TuiCell, TuiHeader} from '@taiga-ui/layout';
import {LocationService} from '../../../services/location.service';
import {UserHistoriesService} from '../../../user_db.services/user-histories.service';
import {catchError, forkJoin, of} from 'rxjs';
import {UUID} from 'node:crypto';
import {HelperService} from '../../../user_db.services/helper.service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-history-one-user',
  imports: [
    NgForOf,
    TuiTableDirective,
    TuiTableTbody,
    TuiTableTd,
    TuiTableTh,
    TuiTitle,
    DatePipe,
    TuiCell,
    RouterLink,
    TuiButton,
    TuiIcon,
    TuiHeader
  ],
  templateUrl: './history-one-user.component.html',
  styleUrl: './history-one-user.component.scss'
})
export class HistoryOneUserComponent {

  protected userHistories: any[];

  constructor(private userHistoriesService: UserHistoriesService,
              private locationService: LocationService,
              private helperService: HelperService) {
    this.userHistories = [];
  }

  ngOnInit(): void {
    this.userHistoriesService.getUserHistoriesByUserId(localStorage.getItem("user_uuid") as UUID).subscribe({
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

          forkJoin([locationObs]).subscribe(([article]) => {
            const articleName = (<any>article).name || 'Unknown';

            this.userHistories.push({
              article_name: articleName,
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
