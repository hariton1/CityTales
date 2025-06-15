import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, signal} from '@angular/core';
import {Router} from '@angular/router';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {
  TuiAppearance,
  TuiAutoColorPipe,
  TuiButton, TuiDialog,
  TuiFallbackSrcPipe,
  TuiIcon,
  TuiScrollbar,
  TuiSurface, TuiTitle
} from '@taiga-ui/core';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserService} from '../../services/user.service';
import {TuiAvatar, TuiCarouselComponent, TuiChevron, TuiPagination, TuiTooltip} from '@taiga-ui/kit';
import {TuiCardCollapsed, TuiCardLarge, TuiHeader} from '@taiga-ui/layout';
import {TuiExpand} from '@taiga-ui/experimental';
import {TuiItem} from '@taiga-ui/cdk';
import {BreakpointService} from '../../services/breakpoints.service';
import {EnrichmentService} from '../../services/enrichment.service';
import { FunFactService, FunFactCardDTO } from '../../services/fun-fact.service';
import {supabase} from '../../user-management/supabase.service';
import {SavedFunFactService} from '../../user_db.services/saved-fun-fact.service';
import {SavedFunFactDto} from '../../user_db.dto/saved-fun-fact.dto';

@Component({
  selector: 'app-historic-event-detail',
  imports: [
    NgForOf,
    NgIf,
    TuiIcon,
    TuiScrollbar,
    AsyncPipe,
    TuiAppearance,
    TuiAutoColorPipe,
    TuiAvatar,
    TuiButton,
    TuiCardLarge,
    TuiCarouselComponent,
    TuiExpand,
    TuiFallbackSrcPipe,
    TuiHeader,
    TuiPagination,
    TuiSurface,
    TuiTitle,
    TuiTooltip,
    TuiCardCollapsed,
    TuiChevron,
    TuiItem,
    TuiDialog
  ],
  templateUrl: './historic-event-detail.component.html',
  styleUrl: './historic-event-detail.component.less'
})
export class HistoricEventDetailComponent implements OnInit {

  protected index1 = 0; //tone slider
  lineWidths = [90, 70, 95, 60, 85, 80, 60, 75, 85, 80];
  isMobile = false;

  customBreakpointLevel: CustomBreakpointLevel = null;

  summary: string = '';
  enrichedContent: string = '';
  enrichmentStarted = false;
  enrichmentLoading = false;
  tonesItemCount = 3;

  public readonly collapsed = signal(true);

  constructor(private savedFunFactService: SavedFunFactService,
              private funFactService: FunFactService,
              private router: Router,
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

  userId: string | null = null;
  ngOnInit() {
    this.breakpointService.level$.subscribe(() => {
      this.tonesItemCount = this.breakpointService.tonesItemCount;
      this.cdr.detectChanges();
    });
    supabase.auth.getSession().then(({ data: { session } }) => {
      if (session?.access_token) {
        supabase.auth.getUser().then(({ data: { user } }) => {
          if (user) {
            this.userId = user.id;
            console.log('User ID:', this.userId);
          }
        });
      }
    });
  }


  @Input()
  get selectedEvent(): any {
    return this._selectedEvent;
  }
  set selectedEvent(value: any) {
    this._selectedEvent = value;
    if (this.userId !== null) {
      this.loadFunFact();
    } else {
      setTimeout(() => this.loadFunFact(), 100);
    }
  }
  private _selectedEvent: any;

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
        wikiId: this.selectedEvent.viennaHistoryWikiId
      }
    });
  }

  startEnrichment(tone: string): void {
    this.enrichmentStarted = true;
    this.enrichmentLoading = true;

    let content = this.selectedEvent.contentGerman;

    this.EnrichmentService.enrichContentWithTone(tone, content).subscribe({
      next: (response) => {
        console.log('tone: ' + response.tone);
        console.log('summary: ' + response.summary);
        console.log('enrichedContent: ' + response.enrichedContent);

        this.summary = response.summary;
        this.enrichedContent = response.enrichedContent;
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
    return Math.ceil(this.selectedEvent?.relatedPersons?.length / this.itemsCount);
  }

  get buildingsPageCount(): number {
    return Math.ceil(this.selectedEvent?.relatedBuildings?.length / this.itemsCount);
  }

  get eventsPageCount(): number {
    return Math.ceil(this.selectedEvent?.relatedEvents?.length / this.itemsCount);
  }

  funFact: FunFactCardDTO | null = null;
  funFactSaved = false;
  funFactSaveError: string = '';
  loadFunFact() {
    this.funFact = null;
    this.funFactSaved = false;

    if (this._selectedEvent && this._selectedEvent.viennaHistoryWikiId) {
      this.funFactService.getBuildingFunFact(this._selectedEvent.viennaHistoryWikiId).subscribe({
        next: fact => {
          this.funFact = fact;
          this.funFactSaved = false;

          if (this.userId && this.funFact && this.funFact.fact) {
            this.savedFunFactService.getFunFactsByUserId(this.userId as any).subscribe(savedFacts => {
              this.funFactSaved = savedFacts.some((sf: SavedFunFactDto) =>
                ((sf.getHeadline?.() ?? sf['headline']) === (this.selectedEvent?.name ?? '')) &&
                ((sf.getFunFact?.() ?? sf['fun_fact']) === this.funFact?.fact)
              );
              this.cdr.markForCheck();
            });
          }
        },
        error: () => {
          this.funFact = null;
          this.funFactSaved = false;
          this.cdr.markForCheck();
        }
      });
    } else {
      this.funFact = null;
      this.funFactSaved = false;
    }
  }


  saveFunFact() {
    if (!this.funFact || this.funFactSaved) return;

    if (!this.userId) {
      alert('User nicht eingeloggt! Speichern nicht möglich.');
      return;
    }

    const savedFunFact = new SavedFunFactDto(
      0,
      this.userId as any,
      this.selectedEvent?.id ?? this.selectedEvent?.viennaHistoryWikiId,
      this.selectedEvent?.name,
      this.funFact.fact,
      this.selectedEvent?.imageUrls?.[0] || '',
      this.funFact.score,
      '', // reason (leer)
    );

    this.funFactSaveError = '';
    this.savedFunFactService.createNewSavedFunFact(savedFunFact).subscribe({
      next: () => {
        this.funFactSaved = true;
        this.cdr.markForCheck();
      },
      error: () => {
        this.funFactSaveError = 'Speichern fehlgeschlagen. Bitte versuchen Sie es erneut!';
      }
    });
  }

  shareDialogOpen = false;

  get shareText(): string {
    return `${this.selectedEvent.name}: ${this.funFact?.fact} — via CityTales\n${window.location.href}`;
  }

  shareWhatsApp() {
    const url = 'https://wa.me/?text=' + encodeURIComponent(this.shareText);
    window.open(url, '_blank');
  }

  shareFacebook() {
    const url = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(window.location.href)}&quote=${encodeURIComponent(this.shareText)}`;
    window.open(url, '_blank');
  }

  shareTwitter() {
    const url = `https://twitter.com/intent/tweet?text=${encodeURIComponent(this.shareText)}`;
    window.open(url, '_blank');
  }

  shareMail() {
    const subject = encodeURIComponent(`CityTales: ${this.selectedEvent.name}`);
    const body = encodeURIComponent(this.shareText);
    const url = `mailto:?subject=${subject}&body=${body}`;
    window.open(url, '_blank');
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
