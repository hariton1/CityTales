import { Component, Input } from '@angular/core';
import { HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {HistoricPlaceDetailComponent} from '../historic-place-detail/historic-place-detail.component';

@Component({
  selector: 'app-sidebar',
  imports: [
    HistoricPlaceDetailComponent
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {

  @Input() selectedPlace: any;

  setSelectedPlace(place: HistoricalPlaceEntity) {
    this.selectedPlace = place;
  }

}
