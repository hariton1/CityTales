import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  Output,
  ViewChild
} from '@angular/core';
import {HistoricPlaceDetailComponent} from '../historic-place-detail/historic-place-detail.component';
import {HistoricPlacePreviewComponent} from '../historic-place-preview/historic-place-preview.component';
import {NgIf, CommonModule} from '@angular/common';
import {TuiSearchResults} from '@taiga-ui/experimental';
import {ReactiveFormsModule, FormControl} from '@angular/forms';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {EnrichmentService} from '../../services/enrichment.service';
import {TuiTitle, TuiTextfield, TuiScrollbar, TuiLoader} from '@taiga-ui/core';
import {TuiCell, TuiInputSearch} from '@taiga-ui/layout';
import {debounceTime, filter, Observable, switchMap} from 'rxjs';
import {SearchService} from '../../services/search.service';
import {UserService} from '../../services/user.service';
import {NotificationInboxComponent} from '../../core/notification-inbox/notification-inbox.component';
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
    TuiScrollbar,
    TuiLoader
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.less'
})
export class SidebarComponent implements OnChanges{

  @Input() selectedPlace: BuildingEntity | null = null;
  @Input() selectedPerson: PersonEntity | null = null;
  @Input() selectedEvent: EventEntity | null = null;

  @Output() onCloseDetailView = new EventEmitter();
  @Output() onSelectEvent:EventEmitter<any> = new EventEmitter<any>();


  @Input() historicalPlaces: BuildingEntity[] = [];
  public isLoading = true;

  summary: string = '';
  enrichedContent: string = '';
  enrichmentStarted = false;
  enrichmentLoading = false;

  constructor(readonly EnrichmentService: EnrichmentService, readonly searchService: SearchService,
              private userService: UserService,
              readonly breakpointService: BreakpointService) {
  }

  get isMobile(): boolean {
    return ['mobile', 'tablet'].includes(this.breakpointService.currentLevel ?? '');
  }

  selectedItem: { type: 'place' | 'event' | 'person'; data: any } | null = null;

  ngOnChanges(): void {
    let shouldScroll = false;

    if (this.selectedPlace) {
      this.selectedItem = { type: 'place', data: this.selectedPlace };
      shouldScroll = true;
    } else if (this.selectedPerson) {
      this.selectedItem = { type: 'person', data: this.selectedPerson };
      shouldScroll = true;
    } else if (this.selectedEvent) {
      this.selectedItem = { type: 'event', data: this.selectedEvent };
      shouldScroll = true;
    } else {
      this.selectedItem = null;
    }

    if (shouldScroll) {
      this.scrollToTop();
    }

    if (this.historicalPlaces && this.historicalPlaces.length > 0) {
      this.isLoading = false;
    }
  }

  setPlaceDetail(place: BuildingEntity) {
    this.selectedItem = { type: 'place', data: place };
    this.onSelectEvent.emit(place);
    this.scrollToTop();
  }

  setPersonDetail(person: PersonEntity) {
    this.selectedItem = { type: 'person', data: person };
    this.scrollToTop();
  }

  setEventDetail(event: EventEntity) {
    this.selectedItem = { type: 'event', data: event };
    this.scrollToTop();
  }

  closeDetailView() {
    this.selectedItem = null;
    this.onCloseDetailView.emit();
  }

  @ViewChild('scrollbarRef') scrollbarRef!: ElementRef<HTMLElement>;
  private scrollToTop(): void {
    setTimeout(() => {
      if (this.scrollbarRef?.nativeElement) {
        this.scrollbarRef.nativeElement.scrollTop = 0;
      }
    });
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

  onItemClick(item: any) {
    if (item.buildingType != null) {
      const buildingId = item.viennaHistoryWikiId;
      this.searchService.getBuildingById(buildingId).subscribe(building => {
        this.setPlaceDetail(building);
      });
    } else if (item.birthDate != null) {
      const personId = item.viennaHistoryWikiId;
      this.searchService.getPersonById(personId).subscribe(person => {
        this.setPersonDetail(person);
      });
    } else if (item.organizer != null) {
      const eventId = item.viennaHistoryWikiId;
      this.searchService.getEventById(eventId).subscribe(event => {
        this.setEventDetail(event);
      });
    } else {
      // Fallback für direkt geladene Entitäten
      if (item.latitude != null && item.longitude != null) {
      } else if (item.birthDate != null) {
        this.setPersonDetail(item);
      } else if (item.organizer != null) {
        this.setEventDetail(item);
      }
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
