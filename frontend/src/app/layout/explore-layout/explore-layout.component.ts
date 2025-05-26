import { Component } from '@angular/core';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {MapViewComponent} from '../map-view/map-view.component';
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';

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

  selectedPlace: BuildingEntity | null = null;
  historicalPlaces: BuildingEntity[] = [];
  setDetailedView: boolean = false;

  setSelectedPlace(place: BuildingEntity) {
    this.selectedPlace = place;
  }

  setHistoricalPlaces(places: BuildingEntity[]) {
    this.historicalPlaces = places;
  }

  setDetailedViewEvent(value: boolean) {
    this.setDetailedView = value;
  }
}
