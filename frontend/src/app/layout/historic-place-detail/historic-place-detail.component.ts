import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule, NgIf} from '@angular/common';
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {PersonService} from '../../services/person.service';
import {HistoricalPersonEntity} from '../../dto/db_entity/HistoricalPersonEntity';

@Component({
  selector: 'app-historic-place-detail',
  imports: [
    NgIf,
    CommonModule
  ],
  templateUrl: './historic-place-detail.component.html',
  styleUrl: './historic-place-detail.component.scss'
})
export class HistoricPlaceDetailComponent {

  constructor(private personService: PersonService) {
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
      this.setDetailEvent.emit(false);
  }
}
