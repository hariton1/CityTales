import {
  ChangeDetectionStrategy,
  Component,
  effect,
  EventEmitter,
  inject,
  Input,
  Output,
  signal,
  ChangeDetectorRef,
  OnInit
} from '@angular/core';
import {CommonModule, NgIf} from '@angular/common';
import {
  TuiAppearance,
  TuiAutoColorPipe,
  TuiButton,
  TuiDialog, TuiFallbackSrcPipe,
  TuiHint,
  TuiIcon,
  TuiLabel, TuiLink,
  TuiScrollbar, TuiSurface,
  TuiTextfieldComponent, TuiTextfieldDirective, TuiTextfieldOptionsDirective, TuiTitle
} from '@taiga-ui/core';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';
import { Router } from '@angular/router';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserService} from '../../services/user.service';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {PricesService} from '../../services/prices.service';
import {Price} from '../../user_db.dto/price.dto';
import {
  maskitoAddOnFocusPlugin,
  maskitoCaretGuard,
  maskitoNumberOptionsGenerator,
  maskitoRemoveOnBlurPlugin
} from '@maskito/kit';
import {TuiAutoFocus, TuiItem, TuiPlatform} from '@taiga-ui/cdk';
import {TuiInputModule, TuiTextfieldControllerModule} from '@taiga-ui/legacy';
import {TuiAvatar, TuiCarouselComponent, TuiChevron, TuiPagination, TuiTooltip} from '@taiga-ui/kit';
import {MaskitoDirective} from '@maskito/angular';
import {TuiResponsiveDialogOptions} from '@taiga-ui/addon-mobile';
import {MaskitoOptions} from '@maskito/core';
import {UUID} from 'node:crypto';
import {UserDto} from '../../user_db.dto/user.dto';
import {TuiCard, TuiHeader} from '@taiga-ui/layout';
import {TuiExpand} from '@taiga-ui/experimental';
import {EnrichmentService} from '../../services/enrichment.service';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {BreakpointService} from '../../services/breakpoints.service';

const postfix = ' €';
const numberOptions = maskitoNumberOptionsGenerator({
  postfix,
  decimalSeparator: '.',
  maximumFractionDigits: 2,
  min: 0,
});

@Component({
  selector: 'app-historic-place-detail',
  imports: [
    NgIf,
    CommonModule,
    TuiAutoFocus,
    TuiDialog,
    TuiHint,
    TuiScrollbar,
    TuiIcon,
    TuiButton,
    FormsModule,
    TuiInputModule,
    ReactiveFormsModule,
    TuiLabel,
    TuiTextfieldComponent,
    TuiTextfieldControllerModule,
    TuiTextfieldDirective,
    TuiTextfieldOptionsDirective,
    TuiTooltip,
    MaskitoDirective,
    TuiCard,
    TuiHeader,
    TuiTitle,
    TuiLink,
    TuiChevron,
    TuiExpand,
    TuiSurface,
    TuiCarouselComponent,
    TuiPlatform,
    TuiItem,
    TuiAvatar,
    TuiFallbackSrcPipe,
    TuiAutoColorPipe,
    TuiAppearance,
    TuiPagination
  ],
  templateUrl: './historic-place-detail.component.html',
  styleUrl: './historic-place-detail.component.less',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoricPlaceDetailComponent implements OnInit{

  private readonly pricesService = inject(PricesService);
  locationId= signal(0);
  prices = this.pricesService.prices;
  public readonly collapsed = signal(true); //for collapsed card
  protected index1 = 0; //tone slider
  lineWidths = [90, 70, 95, 60, 85, 80, 60, 75, 85, 80];
  isMobile = false;

  customBreakpointLevel: CustomBreakpointLevel = null;


  summary: string = '';
  enrichedContent: string = '';
  enrichmentStarted = false;
  enrichmentLoading = false;
  tonesItemCount = 3;


  constructor(private userService: UserService,
              private userHistoriesService: UserHistoriesService,
              private router: Router,
              readonly EnrichmentService: EnrichmentService,
              readonly cdr: ChangeDetectorRef,
              readonly breakpointObserver: BreakpointObserver,
              private breakpoint: BreakpointService,) {
    effect(() => {
      this.pricesService.getPricesByLocation(this.locationId());
      this.cdr.markForCheck();
    })
    let userId = localStorage.getItem("user_uuid") as UUID;
    this.userService.getUserWithRoleById(userId).subscribe((user) => {
      this.initPrices(user);
    });
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
    this.breakpoint.level$.subscribe(() => {
      this.tonesItemCount = this.breakpoint.tonesItemCount;
      this.cdr.detectChanges();
    });
  }


  protected dialogLabel = '';
  protected options: Partial<TuiResponsiveDialogOptions> = {};
  protected userHasPermission = false;
  protected readonly maskitoOptions: MaskitoOptions = {
    ...numberOptions,
    plugins: [
      ...numberOptions.plugins,
      maskitoCaretGuard((value) => [0, value.length - postfix.length]),
      maskitoAddOnFocusPlugin(postfix),
      maskitoRemoveOnBlurPlugin(postfix),
    ],
  };

  protected priceForm = new FormGroup({
    priceId: new FormControl<number | null>(null),
    priceFormValue: new FormControl(''),
    nameFormValue: new FormControl(''),
    descriptionFormValue: new FormControl('')
  });

  protected open = false;

  protected showDialog(priceIndex: number): void {
    if (priceIndex >= 0) {
      let price = this.pricesService.prices()[priceIndex];
      this.priceForm.get('priceId')?.setValue(price.priceId ? price.priceId : null);
      this.priceForm.get('priceFormValue')?.setValue(price.price ? price.price.toString() : null);
      this.priceForm.get('nameFormValue')?.setValue(price.name ? price.name : null);
      this.priceForm.get('descriptionFormValue')?.setValue(price.description ? price.description : null);
    } else {
      this.priceForm.get('priceId')?.setValue(null);
      this.priceForm.get('priceFormValue')?.setValue(null);
      this.priceForm.get('nameFormValue')?.setValue(null);
      this.priceForm.get('descriptionFormValue')?.setValue(null);
    }

    this.dialogLabel = priceIndex < 0 ? 'Add' : 'Edit';
    this.dialogLabel += ' price';
    this.options = {
      label: this.dialogLabel,
      size: 's',
    };

    this.open = true;
  }

  onSubmit(): void {
    let priceId = this.priceForm.get('priceId')?.value;
    let priceValue = this.priceForm.get('priceFormValue')?.value;
    let nameValue = this.priceForm.get('nameFormValue')?.value;
    let descriptionValue = this.priceForm.get('descriptionFormValue')?.value;

    let price = new PriceDTO(priceId, this.selectedPlace.viennaHistoryWikiId, this.getPriceValue(priceValue), nameValue, descriptionValue);
    this.createOrUpdate(price);
  }

  onDelete(): void {
    let priceId = this.priceForm.get('priceId')?.value;
    console.log('delete', priceId);
    let price = this.prices().filter((price) => price.priceId === priceId).at(0);
    this.prices().splice(this.prices().indexOf(price!),1);
    this.deletePrice(priceId!);
  }

  @Input()
  get selectedPlace(): any {
    return this._selectedPlace;
  }
  set selectedPlace(value: any) {
    this._selectedPlace = {
      ...value,
      relatedPersons: value.relatedPersons ?? this._selectedPlace?.relatedPersons,
      relatedBuildings: value.relatedBuildings ?? this._selectedPlace?.relatedBuildings,
      relatedEvents: value.relatedEvents ?? this._selectedPlace?.relatedEvents,
    };

    this.summary = '';
    this.enrichedContent = '';
    this.enrichmentStarted = false;
    this.enrichmentLoading = false;
    this.cdr.markForCheck();
  }
  private _selectedPlace: any;

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
    console.log(this.selectedPlace)
    this._selectedPlace.userHistoryEntry.setCloseDt(new Date());

    this.userHistoriesService.updateUserHistory(this._selectedPlace.userHistoryEntry).subscribe({
        next: (results) => {
          console.log('User history entry updated successfully', results);
          /*this.alerts
            .open('Your user history entry is updated', {label: 'Success!', appearance: 'success', autoClose: 3000})
            .subscribe();*/
        },
        error: (err) => {
          console.error('Error updating user history entry:', err);
        }
    });

    this.onCloseEvent.emit();
    this.summary = '';
    this.enrichedContent = '';
    this.enrichmentStarted = false;
    this.enrichmentLoading = false;
    this.cdr.markForCheck();
  }

  navigateToFeedback(): void {
    this.closeDetail();

    this.router.navigate(['/feedback'], {
      queryParams: {
        wikiId: this.selectedPlace.viennaHistoryWikiId
      }
    });
  }

  getPriceValue(priceValue: string | null | undefined): number {
    if (!priceValue || priceValue === '') {
      return 0;
    }
    if (!priceValue.endsWith(' €')) {
      priceValue += ' €';
    }
    return parseFloat(priceValue.substring(0, priceValue.length-2).replace(',','.'));
  }

  createOrUpdate(price: Price): void {
    this.pricesService.createOrUpdate(price);
  }

  deletePrice(id: number): void {
    this.pricesService.delete(id);
  }

  initPrices(user: UserDto): void {
    this.locationId.set(this.selectedPlace.viennaHistoryWikiId);
    let contributor = true; //TODO change this once Contributor role is assignable
    let emailMatchesLocation = false;

    if(user?.role === 'Contributor'){
      contributor = true;
    }
    if(user?.email.includes('@priceadminmaintainer.com')) {
      // TODO add functionality for museum workers to maintain prices
      emailMatchesLocation = true;
    }

    this.userHasPermission = contributor && emailMatchesLocation;
  }

  startEnrichment(tone: string): void {
    this.enrichmentStarted = true;
    this.enrichmentLoading = true;

    let content = this.selectedPlace.contentGerman;

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
    return Math.ceil(this.selectedPlace?.relatedPersons?.length / this.itemsCount);
  }

  get buildingsPageCount(): number {
    return Math.ceil(this.selectedPlace?.relatedBuildings?.length / this.itemsCount);
  }

  get eventsPageCount(): number {
    return Math.ceil(this.selectedPlace?.relatedEvents?.length / this.itemsCount);
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


class PriceDTO implements Price {
  priceId: number | null | undefined;
  locationId: number | null;
  price: number | null | undefined;
  name: string | null | undefined;
  description: string | null | undefined;
  readonly created_at: Date;

  constructor(priceId: number | null | undefined, locationId: number | null, price: number | null | undefined, name: string | null | undefined, description: string | null | undefined) {
    this.priceId = priceId;
    this.locationId = locationId;
    this.price = price;
    this.name = name;
    this.description = description;
    this.created_at = new Date();
  }
}
