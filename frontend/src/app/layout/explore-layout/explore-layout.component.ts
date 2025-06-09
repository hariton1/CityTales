import {Component, OnInit, ViewChild} from '@angular/core';
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

@Component({
  selector: 'app-explore-layout',
  imports: [
    SidebarComponent,
    MapViewComponent,
    TuiSegmented,
    NgIf,
    TuiIcon,
    NotificationInboxComponent
  ],
  templateUrl: './explore-layout.component.html',
  styleUrl: './explore-layout.component.less'
})
export class ExploreLayoutComponent implements OnInit {

  @ViewChild(MapViewComponent) mapViewComponent!: MapViewComponent;

  currentViewMobile: 'discover' | 'map' = 'discover';
  isMobile = false;


  //Sidebar selectors
  selectedPlace: BuildingEntity | null = null;
  selectedPerson: PersonEntity | null = null;
  selectedEvent: EventEntity | null = null;

  historicalPlaces: BuildingEntity[] = [];

  setDetailedView: boolean = false;

  constructor(readonly breakpointObserver: BreakpointObserver) {
  }

  ngOnInit() {
    this.breakpointObserver
      .observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape])
      .subscribe(result => {
        this.isMobile = result.matches;
        this.currentViewMobile = 'discover';
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

  selectDetailEvent(event: any) {
    if(event.buildingType == 'Gebäude') {
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

  onSelectEvent(event: any) {
    console.log("onSelectEvent: " + event);
    var marker = this.mapViewComponent.location_marker_dict.get(event.viennaHistoryWikiId)

    if(!marker) {
      console.log("Selected location does not have a declared marker!")
      this.mapViewComponent.unselectMarker();
    } else {
      this.mapViewComponent.selectMarker(marker, event)
    }
  }

  onCloseDetailView() {
    console.log("Invoked close detail method in explore layout!")
    this.mapViewComponent.unselectMarker();
  }
}
