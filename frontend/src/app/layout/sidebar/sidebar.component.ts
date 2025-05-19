import { Component, Input } from '@angular/core';
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


  setSelectedPlace(place: any) {
    this.selectedPlace = place;
  }

  setDetailEvent(event: boolean): void {
    this.detailedView = event
    console.log(this.detailedView);
  }

  setHistoricalPlaces(places: HistoricalPlaceEntity[]): void {
    this.historicalPlaces = places;
    console.log(this.historicalPlaces);
  }

}
