import { Component } from '@angular/core';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {MapViewComponent} from '../map-view/map-view.component';
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';

@Component({
  selector: 'app-explore-layout',
  imports: [
    SidebarComponent,
    MapViewComponent
  ],
  templateUrl: './explore-layout.component.html',
  styleUrl: './explore-layout.component.scss'
})
export class ExploreLayoutComponent {

  selectedPlace: HistoricalPlaceEntity | null = null;
  historicalPlaces: HistoricalPlaceEntity[] = [];

  setSelectedPlace(place: HistoricalPlaceEntity) {
    this.selectedPlace = place;
  }

  setHistoricalPlaces(places: HistoricalPlaceEntity[]) {
    this.historicalPlaces = places;
    console.log(places);
  }
}
