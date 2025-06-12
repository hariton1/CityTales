import { Component } from '@angular/core';
import {DatePipe, NgForOf} from "@angular/common";
import {TuiTableDirective, TuiTableTbody, TuiTableTd, TuiTableTh} from "@taiga-ui/addon-table";
import {TuiButton, TuiTitle} from "@taiga-ui/core";
import {TuiStatus} from '@taiga-ui/kit';
import {SavedFunFactService} from '../../../user_db.services/saved-fun-fact.service';
import {SavedFunFactDto} from '../../../user_db.dto/saved-fun-fact.dto';
import {LocationService} from '../../../services/location.service';
import {HelperService} from '../../../user_db.services/helper.service';
import {UUID} from 'node:crypto';
import {catchError, forkJoin, of} from 'rxjs';
import {TuiCell} from '@taiga-ui/layout';

@Component({
  selector: 'app-fun-fact-list',
  imports: [
    DatePipe,
    NgForOf,
    TuiTableDirective,
    TuiTableTbody,
    TuiTableTd,
    TuiTableTh,
    TuiTitle,
    TuiButton,
    TuiStatus,
    TuiCell
  ],
  templateUrl: './fun-fact-list.component.html',
  styleUrl: './fun-fact-list.component.scss'
})
export class FunFactListComponent {

  protected savedFunFacts: any[];

  constructor(private savedFunFactService: SavedFunFactService,
              private locationService: LocationService,
              private helperService: HelperService) {
    this.savedFunFacts = [];
  }

  ngOnInit(): void {
    let user_id = localStorage.getItem('user_id');

    if(user_id !== null) {
      this.savedFunFactService.getFunFactsByUserId(user_id as UUID).subscribe({
        next: (savedFunFacts) => {
          // Process each user history independently
          savedFunFacts.forEach(savedFunFact => {
            // You can process each user history here
            console.log('Processing fun fact:', savedFunFact);
            const locationObs = this.locationService.getLocationByVHWId(savedFunFact.getArticleId())
              .pipe(catchError(error => {
                console.error('Error fetching location:', error);
                return of(null);
              }));

            forkJoin([locationObs]).subscribe(([article]) => {
              const articleName = (<any>article).name || 'Unknown';

              this.savedFunFacts.push({
                article_name: articleName,
                saved_fun_fact_id: savedFunFact.getSavedFunFactId(),
                user_id: savedFunFact.getUserId(),
                article_id: savedFunFact.getArticleId(),
                headline: savedFunFact.getHeadline(),
                fun_fact: savedFunFact.getFunFact(),
                image_url: savedFunFact.getImageUrl(),
                score: savedFunFact.getScore(),
                reason: savedFunFact.getReason(),
                saved_at: this.helperService.sanitizeDate(savedFunFact.getSavedAt())
              });
            });

          });
        },
        error: (error) => {
          console.error('Error fetching fun facts:', error);
        }
      });
    }
  }

  protected handleDeleteClick(savedFunFact: any) {
    this.savedFunFactService.deleteSavedFunFact(new SavedFunFactDto(
      savedFunFact.saved_fun_fact_id,
      savedFunFact.user_id,
      savedFunFact.article_id,
      savedFunFact.headline,
      savedFunFact.fun_fact,
      savedFunFact.image_url,
      savedFunFact.score,
      savedFunFact.reason,
      savedFunFact.saved_at
    ));
  };

}
