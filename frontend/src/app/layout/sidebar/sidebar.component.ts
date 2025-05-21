import {Component, EventEmitter, Input, Output} from '@angular/core';
import { HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {HistoricPlaceDetailComponent} from '../historic-place-detail/historic-place-detail.component';
import {HistoricPlacePreviewComponent} from '../historic-place-preview/historic-place-preview.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-sidebar',
  imports: [
    NgIf,
    HistoricPlaceDetailComponent,
    HistoricPlacePreviewComponent
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {

  @Input() selectedPlace: any;
  @Input() historicalPlaces: HistoricalPlaceEntity[] = [];
  @Input() detailedView: boolean = false;

  @Output() setDetailedView: EventEmitter<boolean> = new EventEmitter<boolean>();


  setSelectedPlace(place: any) {
    this.selectedPlace = place;
    console.log(place.building.name);
  }

  setDetailEvent(event: boolean): void {
    this.detailedView = event
    this.setDetailedView.emit(event);
  }

  setHistoricalPlaces(places: HistoricalPlaceEntity[]): void {
    this.historicalPlaces = places;
    console.log(this.historicalPlaces);
  }

}
