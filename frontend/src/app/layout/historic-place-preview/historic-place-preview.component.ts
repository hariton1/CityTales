import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TuiAlertService, TuiAppearance, TuiScrollbar} from '@taiga-ui/core';
import {TuiCardLarge} from '@taiga-ui/layout';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserHistoryDto} from '../../user_db.dto/user-history.dto';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';
import {UserPointsService} from '../../user_db.services/user-points.service';
import {UserPointDto} from '../../user_db.dto/user-point.dto';


@Component({
  selector: 'app-historic-place-preview',
  imports: [TuiAppearance,
    TuiCardLarge,
    CommonModule, TuiScrollbar
  ],
  templateUrl: './historic-place-preview.component.html',
  styleUrl: './historic-place-preview.component.scss'
})
export class HistoricPlacePreviewComponent {

  private readonly alerts = inject(TuiAlertService);

  @Input() historicalPlaces: BuildingEntity[] = [];
  @Output() selectPlaceEvent: EventEmitter<BuildingEntity> = new EventEmitter<BuildingEntity>();
  @Output() setDetailedViewEvent: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private userHistoriesService: UserHistoriesService,
              private userPointsService: UserPointsService) {
  }

  onDetailsClick(place: BuildingEntity) {
    place.userHistoryEntry = new UserHistoryDto(
      -1,
      "f5599c8c-166b-495c-accc-65addfaa572b",
      Number(place.viennaHistoryWikiId),
      new Date(),
      new Date(0),
      2);

    let newUserPointsEntry = new UserPointDto(
      -1,
      "f5599c8c-166b-495c-accc-65addfaa572b",
      1,
      new Date()
    );

    this.userHistoriesService.createNewUserHistory(place.userHistoryEntry).subscribe({
      next: (results) => {
        console.log('New user history entry created successfully', results);
        place.userHistoryEntry.setUserHistoryId(results.getUserHistoryId());
        /*this.alerts
          .open('Your new user history entry is saved', {label: 'Success!', appearance: 'success', autoClose: 3000})
          .subscribe();*/
      },
      error: (err) => {
        console.error('Error creating user history entry:', err);
      }
    });

    this.userPointsService.createNewPoints(newUserPointsEntry).subscribe({
      next: (results) => {
        console.log('New user points entry created successfully', results);
        /*this.alerts
          .open('Your new user history entry is saved', {label: 'Success!', appearance: 'success', autoClose: 3000})
          .subscribe();*/
      },
      error: (err) => {
        console.error('Error creating user points entry:', err);
      }
    });

    this.selectPlaceEvent.emit(place);
    this.setDetailedViewEvent.emit(true);
  }

}
