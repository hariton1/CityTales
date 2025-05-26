import {Component, OnInit} from '@angular/core';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {MapViewComponent} from '../map-view/map-view.component';
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {TuiSegmented} from '@taiga-ui/kit';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-explore-layout',
  imports: [
    SidebarComponent,
    MapViewComponent,
    TuiSegmented,
    NgIf
  ],
  templateUrl: './explore-layout.component.html',
  styleUrl: './explore-layout.component.less'
})
export class ExploreLayoutComponent implements OnInit {

  currentViewMobile: 'history' | 'map' = 'history';
  isMobile = false;

  selectedPlace: BuildingEntity | null = null;
  historicalPlaces: BuildingEntity[] = [];
  setDetailedView: boolean = false;

  constructor(readonly breakpointObserver: BreakpointObserver) {
  }

  ngOnInit() {
    this.breakpointObserver
      .observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape])
      .subscribe(result => {
        this.isMobile = result.matches;
      });
  }

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
