import {Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, ViewChild} from '@angular/core';
import {HistoricPlaceDetailComponent} from '../historic-place-detail/historic-place-detail.component';
import {HistoricPlacePreviewComponent} from '../historic-place-preview/historic-place-preview.component';
import {NgIf, CommonModule} from '@angular/common';
import {TuiSearchResults} from '@taiga-ui/experimental';
import {ReactiveFormsModule, FormControl} from '@angular/forms';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {EnrichmentService} from '../../services/enrichment.service';
import {TuiAppearance, TuiTitle, TuiTextfield, TuiIcon} from '@taiga-ui/core';
import {TuiCell, TuiInputSearch} from '@taiga-ui/layout';
import {debounceTime, filter, Observable, switchMap} from 'rxjs';
import {SearchService} from '../../services/search.service';
import {UserService} from '../../services/user.service';
import {NotificationInboxComponent} from '../../core/notification-inbox/notification-inbox.component';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';
import {HistoricEventDetailComponent} from '../historic-event-detail/historic-event-detail.component';
import {HistoricPersonDetailComponent} from '../historic-person-detail/historic-person-detail.component';
import {BreakpointService} from '../../services/breakpoints.service';

@Component({
  selector: 'app-sidebar',
  imports: [
    NgIf,
    HistoricPlaceDetailComponent,
    HistoricPlacePreviewComponent,
    TuiAppearance,
    TuiTitle,
    ReactiveFormsModule,
    TuiCell,
    TuiTextfield,
    TuiInputSearch,
    TuiSearchResults,
    CommonModule,
    NotificationInboxComponent,
    HistoricEventDetailComponent,
    HistoricPersonDetailComponent,
    TuiIcon
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.less'
})
export class SidebarComponent implements OnInit{

  @Input() selectedPlace: BuildingEntity | null = null;
  @Input() selectedPerson: PersonEntity | null = null;
  @Input() selectedEvent: EventEntity | null = null;


  @Input() historicalPlaces: BuildingEntity[] = [];

  summary: string = '';
  enrichedContent: string = '';
  enrichmentStarted = false;
  enrichmentLoading = false;

  constructor(readonly EnrichmentService: EnrichmentService, readonly searchService: SearchService,
              private userService: UserService,
              readonly breakpointService: BreakpointService,) {
  }

  ngOnInit() {

  }

  get isMobile(): boolean {
    return ['mobile', 'tablet'].includes(this.breakpointService.currentLevel ?? '');
  }

  setSelectedPlace(place: any) {
    this.selectedPlace = place;
    console.log(this.selectedPlace);
    console.log(this.selectedEvent);
    console.log(this.selectedPerson);
  }

  setPlaceDetail(place: BuildingEntity) {
    this.selectedPlace = place;
    this.selectedEvent = null;
    this.selectedPerson = null;
  }

  setPersonDetail(person: PersonEntity) {
    console.log('Received person:', person);
    this.selectedPlace = null;
    this.selectedEvent = null;
    this.selectedPerson = person;
  }

  setEventDetail(event: EventEntity) {
    this.selectedEvent = event;
    this.selectedPlace = null;
    this.selectedPerson = null;
  }

  closeDetailView(): void {
    this.selectedPlace = null;
    this.selectedEvent = null;
    this.selectedPerson = null;
  }


  //Search field

  protected readonly control = new FormControl('');

  @Input() searchOpen = false;
  @Output() searchOpenChange = new EventEmitter<boolean>();

  lastSearches: string[] = [];

  results$ = this.control.valueChanges.pipe(
    debounceTime(300),
    filter((query: string | null): query is string =>
      typeof query === 'string' && query.length > 0),// Add debounce to prevent too many requests
    filter(query => !!query), // Filter out empty/null queries
    switchMap(query => {
      return new Observable<Record<string, any[]>>(observer => {
        const results: Record<string, any[]> = {};

        // Create an array to store our subscription
        const subscriptions = [
          this.searchService.searchLocation(query).subscribe({
            next: (buildings) => {
              results['Buildings'] = buildings;
              // Check if we have both results
              if ('Persons' in results) {
                observer.next(results);
              }
            },
            error: (error) => observer.error(error)
          }),

          this.searchService.searchPersons(query).subscribe({
            next: (persons) => {
              results['Persons'] = persons;
              // Check if we have both results
              if ('Events' in results) {
                observer.next(results);
              }
            },
            error: (error) => observer.error(error)
          }),

          this.searchService.searchEvents(query).subscribe({
            next: (events) => {
              results['Events'] = events;
              // Check if we have both results
              if ('Buildings' in results) {
                observer.next(results);
              }
            },
            error: (error) => observer.error(error)
          })
        ];

        // Return cleanup function
        return () => {
          subscriptions.forEach(sub => sub.unsubscribe());
        };
      });
    })
  );

  onItemClick(item: any){
    //check if item is a building
    if(item.latitude != null && item.longitude != null){
      this.setPlaceDetail(item)
    } else if (item.birthDate != null) {
      //item is person
      this.setPersonDetail(item)
    } else if (item.organizer != null) {
      //item is event
      this.setEventDetail(item)
    }
  }

  //close the search bar when clicking outside
  @ViewChild('searchFieldRef') searchFieldRef!: ElementRef;
  @HostListener('document:click', ['$event'])
  handleOutsideClick(event: MouseEvent): void {
    setTimeout(() => {
      const target = event.target as HTMLElement;

      const isInsideField = this.searchFieldRef?.nativeElement.contains(target);
      const isInsideDropdown = target.closest('.search-field');

      // Only close if the click was completely outside
      if (!isInsideField && !isInsideDropdown) {
        this.searchOpen = false;
        this.searchOpenChange.emit(false);
      }
    });
  }

}
