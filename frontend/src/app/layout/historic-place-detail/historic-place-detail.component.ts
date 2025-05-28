import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {CommonModule, NgIf} from '@angular/common';
import {PersonService} from '../../services/person.service';
import {HistoricalPersonEntity} from '../../dto/db_entity/HistoricalPersonEntity';
import {TuiAlertService, TuiScrollbar} from '@taiga-ui/core';
import {UserHistoryDto} from '../../user_db.dto/user-history.dto';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';

@Component({
  selector: 'app-historic-place-detail',
  imports: [
    NgIf,
    CommonModule,
    TuiScrollbar
  ],
  templateUrl: './historic-place-detail.component.html',
  styleUrl: './historic-place-detail.component.scss'
})
export class HistoricPlaceDetailComponent {

  private readonly alerts = inject(TuiAlertService);

  constructor(private personService: PersonService,
              private userHistoriesService: UserHistoriesService) {
  }

  private associatedPersons: HistoricalPersonEntity[] = [];

  @Input()
  get selectedPlace(): any {
    return this._selectedPlace;
  }
  set selectedPlace(value: any) {
    this._selectedPlace = value;
  }
  private _selectedPlace: any;

  @Output() setDetailEvent: EventEmitter<boolean> = new EventEmitter<boolean>();

  closeDetail():void{
    this._selectedPlace.userHistoryEntry.setCloseDt(new Date());

    this.userHistoriesService.updateUserHistory(this._selectedPlace.userHistoryEntry).subscribe({
        next: (results) => {
          console.log('User history entry updated successfully', results);
          this.alerts
            .open('Your user history entry is updated', {label: 'Success!', appearance: 'success', autoClose: 3000})
            .subscribe();
        },
        error: (err) => {
          console.error('Error updating user history entry:', err);
        }
    });

    this.setDetailEvent.emit(false);
  }
}
