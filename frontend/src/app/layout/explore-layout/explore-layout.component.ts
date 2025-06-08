import {Component, OnInit} from '@angular/core';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {MapViewComponent} from '../map-view/map-view.component';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {TuiSegmented} from '@taiga-ui/kit';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {NgIf} from '@angular/common';
import {TuiIcon} from '@taiga-ui/core';
import {NotificationInboxComponent} from '../../core/notification-inbox/notification-inbox.component';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';
import { FormsModule } from '@angular/forms';
import { InterestFilterService } from '../../services/interest-filter.service';

@Component({
  selector: 'app-explore-layout',
  imports: [
    SidebarComponent,
    MapViewComponent,
    TuiSegmented,
    NgIf,
    TuiIcon,
    FormsModule,
    NotificationInboxComponent
  ],
  templateUrl: './explore-layout.component.html',
  styleUrl: './explore-layout.component.less'
})
export class ExploreLayoutComponent implements OnInit {

  currentViewMobile: 'discover' | 'map' = 'discover';
  isMobile = false;
  interestFiltering: boolean = true;

  selectedPlace: BuildingEntity | null = null;
  selectedPerson: PersonEntity | null = null;
  selectedEvent: EventEntity | null = null;

  historicalPlaces: BuildingEntity[] = [];
  setDetailedView: boolean = false;

  constructor(
    private interestFilterService: InterestFilterService,
    readonly breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit() {
    this.interestFilterService.filtering$.subscribe(
      value => this.interestFiltering = value
    );

    this.breakpointObserver
      .observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape])
      .subscribe(result => {
        this.isMobile = result.matches;
        this.currentViewMobile = 'discover';
      });
  }

  onInterestFilteringChange(value: boolean) {
    this.interestFilterService.setFiltering(value);
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

  selectDetailEvent(event: any) {
    if(event.type == 'building') {
      this.selectedPlace = event;
      this.selectedPerson = null;
      this.selectedEvent = null;
    }

    if(event.type == 'person') {
      this.selectedPlace = null;
      this.selectedPerson = event;
      this.selectedEvent = null;
    }

    if(event.organizer != null) {
      this.selectedPlace = null;
      this.selectedPerson = null;
      this.selectedEvent = event;
    }

    console.log(this.selectedEvent)
    console.log(this.selectedPlace)
    console.log(this.selectedPerson);
  }
}
