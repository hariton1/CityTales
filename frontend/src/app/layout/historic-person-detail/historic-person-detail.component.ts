import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  signal,
  SimpleChanges
} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {
  TuiAppearance,
  TuiAutoColorPipe,
  TuiButton,
  TuiFallbackSrcPipe,
  TuiIcon,
  TuiScrollbar, TuiSurface,
  TuiTitle
} from "@taiga-ui/core";
import {HistoricalPersonEntity} from '../../dto/db_entity/HistoricalPersonEntity';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';
import {Router} from '@angular/router';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserService} from '../../services/user.service';
import {TuiCardCollapsed, TuiCardLarge, TuiHeader} from '@taiga-ui/layout';
import {TuiExpand} from '@taiga-ui/experimental';
import {TuiAvatar, TuiCarouselComponent, TuiChevron, TuiPagination, TuiTooltip} from '@taiga-ui/kit';
import {BreakpointService} from '../../services/breakpoints.service';
import {EnrichmentService} from '../../services/enrichment.service';
import {TuiItem} from '@taiga-ui/cdk';

@Component({
  selector: 'app-historic-person-detail',
  imports: [
    NgForOf,
    NgIf,
    TuiIcon,
    TuiScrollbar,
    TuiButton,
    TuiCardLarge,
    TuiExpand,
    TuiHeader,
    TuiTitle,
    TuiChevron,
    TuiCardCollapsed,
    AsyncPipe,
    TuiAppearance,
    TuiAutoColorPipe,
    TuiAvatar,
    TuiCarouselComponent,
    TuiFallbackSrcPipe,
    TuiPagination,
    TuiSurface,
    TuiTooltip,
    TuiItem,
    NgSwitchCase,
    NgSwitch
  ],
  templateUrl: './historic-person-detail.component.html',
  styleUrl: './historic-person-detail.component.less'
})
export class HistoricPersonDetailComponent implements OnInit, OnChanges{

  protected index1 = 0; //tone slider
  lineWidths = [90, 70, 95, 60, 85, 80, 60, 75, 85, 80];
  lineWidths2 = [90, 70, 95, 60, 85, 80];
  isMobile = false;

  customBreakpointLevel: CustomBreakpointLevel = null;

  summary: string = '';
  summaryStarted = false;
  summaryLoading = false;

  enrichedContent: string = '';
  selectedTone: string = '';
  enrichmentStarted = false;
  enrichmentLoading = false;
  tonesItemCount = 3;

  public readonly collapsed = signal(true);

  constructor(private router: Router,
              private userService: UserService,
              readonly EnrichmentService: EnrichmentService,
              readonly cdr: ChangeDetectorRef,
              private breakpointService: BreakpointService) {
  }

  tones = [
    { key: 'academic', label: 'Historian', bg: 'historian-bg.png' },
    { key: 'tour', label: 'Tour Guide', bg: 'tour-guide-bg.png' },
    { key: 'poetic', label: 'Poetic', bg: 'poetic-bg.png' },
    { key: 'dramatic', label: 'Dramatic', bg: 'dramatic-bg.png' },
    { key: 'child-friendly', label: 'Child-Friendly', bg: 'child-friendly-bg.png' },
    { key: 'funny', label: 'Funny', bg: 'funny-bg.jpg' },
  ];

  ngOnInit() {
    this.breakpointService.level$.subscribe(() => {
      this.tonesItemCount = this.breakpointService.tonesItemCount;
      this.generateSummary();
      this.cdr.detectChanges();
    });
  }

  @Input()
  get selectedPerson(): any {
    return this._selectedPerson;
  }
  set selectedPerson(value: any) {
    this._selectedPerson = value;
  }
  private _selectedPerson: any;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedPerson']?.currentValue?.contentGerman) {
      this.generateSummary();
    }
  }

  @Output() onPersonDetailEvent: EventEmitter<PersonEntity> = new EventEmitter<PersonEntity>();
  @Output() onEventDetailEvent: EventEmitter<EventEntity> = new EventEmitter<EventEntity>();
  @Output() onBuildingDetailEvent: EventEmitter<BuildingEntity> = new EventEmitter<BuildingEntity>();
  @Output() onCloseEvent: EventEmitter<void> = new EventEmitter();


  onPersonClick(person: PersonEntity) {
    console.log("Emitted Person event: " + person);
    this.onPersonDetailEvent.emit(person);
  }

  onEventClick(event: EventEntity) {
    this.onEventDetailEvent.emit(event);
  }

  onBuildingClick(buildingEntity: BuildingEntity) {
    buildingEntity = this.userService.enterHistoricNode(buildingEntity);
    this.onBuildingDetailEvent.emit(buildingEntity);
  }

  closeDetail():void{
    this.onCloseEvent.emit();
  }

  navigateToFeedback(): void {
    this.closeDetail();

    this.router.navigate(['/feedback'], {
      queryParams: {
        wikiId: this.selectedPerson.viennaHistoryWikiId
      }
    });
  }

  generateSummary(): void {
    this.summaryStarted = true;
    this.summaryLoading = true;

    let content = this.selectedPerson.contentGerman;

    this.EnrichmentService.generateSummary(content).subscribe({
      next: (response) => {
        console.log('summary: ' + response.summary);
        this.summary = response.summary;
      },
      error: (error: any) => {
        console.error(error);
      },
      complete: () => {
        console.log('Completed');
        this.summaryLoading = false;
        this.cdr.detectChanges();
      }
    })
  }

  startEnrichment(tone: string): void {
    this.enrichmentStarted = true;
    this.enrichmentLoading = true;

    let content = this.selectedPerson.contentGerman;

    this.EnrichmentService.enrichContentWithTone(tone, content).subscribe({
      next: (response) => {
        console.log('tone: ' + response.tone);
        console.log('enrichedContent: ' + response.enrichedContent);

        this.enrichedContent = response.enrichedContent;
        this.selectedTone = response.tone;
      },
      error: (error: any) => {
        console.error(error);
      },
      complete: () => {
        console.log('Completed');
        this.enrichmentLoading = false;
        this.cdr.detectChanges();
      }
    })
  }

  protected readonly itemsCount = 3;
  protected index2 = 0; //related persons slider
  protected index3 = 0; //related buildings slider
  protected index4 = 0; //related events slider

  protected get roundedPersons(): number {
    return Math.floor(this.index2 / this.itemsCount);
  }

  protected onIndexPersons(index: number): void {
    this.index2 = index * this.itemsCount;
    this.cdr.detectChanges();
  }

  protected get roundedBuildings(): number {
    return Math.floor(this.index3 / this.itemsCount);
  }

  protected onIndexBuildings(index: number): void {
    this.index3 = index * this.itemsCount;
    this.cdr.detectChanges();
  }

  protected get roundedEvents(): number {
    return Math.floor(this.index4 / this.itemsCount);
  }

  protected onIndexEvents(index: number): void {
    this.index4 = index * this.itemsCount;
    this.cdr.detectChanges();
  }

  get personsPageCount(): number {
    return Math.ceil(this.selectedPerson?.relatedPersons?.length / this.itemsCount);
  }

  get buildingsPageCount(): number {
    return Math.ceil(this.selectedPerson?.relatedBuildings?.length / this.itemsCount);
  }

  get eventsPageCount(): number {
    return Math.ceil(this.selectedPerson?.relatedEvents?.length / this.itemsCount);
  }
}

type CustomBreakpointLevel =
  | 'mobile'          // 360–767px
  | 'tablet'          // 768–1023px
  | 'desktop'         // 1024–1279px
  | '1280-1499'
  | '1500-1899'
  | '1900-2559'
  | '2560+'
  | 'unknown'
  | null;
