import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HistoricPlaceDetailComponent} from '../historic-place-detail/historic-place-detail.component';
import {HistoricPlacePreviewComponent} from '../historic-place-preview/historic-place-preview.component';
import {NgIf, CommonModule} from '@angular/common';
import {TuiSearchResults} from '@taiga-ui/experimental';
import {ReactiveFormsModule, FormControl} from '@angular/forms';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {EnrichmentService} from '../../services/enrichment.service';
import {TuiAppearance, TuiTitle, TuiTextfield} from '@taiga-ui/core';
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
    HistoricPersonDetailComponent
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.less'
})
export class SidebarComponent implements OnInit{

  selectedPlace: BuildingEntity | null = null;
  selectedPerson: PersonEntity | null = null;
  selectedEvent: EventEntity | null = null;


  @Input() historicalPlaces: BuildingEntity[] = [];
  @Input() detailedView: boolean = false;

  summary: string = '';
  enrichedContent: string = '';
  enrichmentStarted = false;
  enrichmentLoading = false;
  isMobile = false;

  constructor(readonly EnrichmentService: EnrichmentService, readonly searchService: SearchService,
              private userService: UserService,
              readonly breakpointObserver: BreakpointObserver) {
  }

  ngOnInit() {
    this.breakpointObserver
      .observe([Breakpoints.HandsetPortrait, Breakpoints.HandsetLandscape])
      .subscribe(result => {
        this.isMobile = result.matches;
      });
  }

  onClick(tone: string): void {
    this.enrichmentStarted = true;
    this.enrichmentLoading = true;

    let content = "Franziskanerkirche (1., Franziskanerplatz; heiliger Hieronymus; Franziskanerkloster).\n" +
      "\n" +
      "Der Orden der Franziskaner kam 1451 nach Wien und richtete den ersten Konvent in St. Theobald auf der Laimgrube ein (Theobaldkirche). Am 10. Mai 1589 wurde den Franziskanermönchen, die während der ersten Belagerung Wiens durch die Osmanen im Jahr 1529 (sogenannte Erste Türkenbelagerung) dieses Stammhaus verloren hatten, das Büßerinnenkloster zu St. Hieronymus übergeben. Sie ließen es größtenteils niederreißen und einige ihnen geschenkte kleine Nachbarhäuser ebenfalls demolieren. Am 14. August 1603 wurde der Grundstein zur neuen, in Formen süddeutscher Renaissance mit starken gotischen Nachklängen erbauten Kirche gelegt, die mit der alten kleinen Hieronymuskapelle im Büßerinnenkloster vereinigt, jedoch an die Ecke zur Weihburggasse hin situiert wurde (Franziskanerplatz). Die Pläne stammten möglicherweise von Pater Bonaventura Daum(ius), der zu dieser Zeit mehrfach zum Ordensprovinzial und Guardian gewählt wurde, aber schon 1619 starb. Die Kirche wurde am 11. Dezember 1611 geweiht. Obwohl sich damals bereits der Einfluss der römischen Barockarchitektur durchsetzte, ist die Franziskanerkirche noch ein Werk spätmittelalterlicher Baugesinnung.\n" +
      "\n" +
      "Der Bau der Franziskanerkirche bildete in Wien den Auftakt der \"Klosteroffensive\" Kardinal Melchior Khlesls zur Wiederbelebung des katholischen Glaubens. Da das Gotteshaus bald großen Zulauf hatte, musste das Problem der Zufahrt gelöst werden. Ordensgeneral Sebastian Didaker richtete am 12. Jänner 1621 an Ferdinand II. einen Bericht, in dem er als Geschenk ein Haus erbat, das man gegen das dicht gegenüber der Franziskanerkirche liegende Oellerische Stiftungshaus eintauschen könne. Tatsächlich wurde dieses 1624 abgerissen (Franziskanerplatz mit Mosesbrunnen [1798]). Wie beliebt die Franziskanerkirche war, lässt sich an der für das Jahr 1714 erhaltenen Zahlen der gelesenen Messen erkennen. Übertroffen wurde die Franziskanerkirche (22.250) dabei nur vom Stephansdom (44.296), der oberen Jesuitenkirche (23.800) sowie der unteren Jesuitenkirche (Universitätskirche; 23.344). Nach der Franziskanerkirche folgte erst mit großem Abstand die Annakirche (1) mit 9.900 Messen.\n" +
      "\n" +
      "Zwischen 1783 und 1792 war die Franziskanerkirche vorübergehend eine eigene Pfarre. Besonderen Zulauf hatte sie zur Zeit des Wiener Kongresses (1814/1815), als hier Zacharias Werner predigte. Ein Zeitgenosse erwähnt, dass er zuvor wegen der Derbheit seiner Predigten von den Augustinern (Augustinerkirche) und Michaelern (Michaelerkirche) abgewiesen worden war. Weiters schreibt er: \"Eines Tages gewahre ich in der Kärntnerstraße eine auffallend hagere Gestalt im Kostüm eines Weltgeistlichen [...]. Das lederen Antlitz mit den tiefliegenden Augen und ordnungslos wehenden Haar ließ mich den famosen Bußprediger erkennen, und ihm auf dem Fuße folgend, gelangte ich in die Franziskanerkirche, wo mich das aus den besten Ständen zahlreich versammelte, großenteils weibliche Publikum über das, was bevorstand, nicht im Zweifel ließ. Nach etwa einer halben Stunde erschien Werner wirklich auf der Kanzel, um sie, wie jedesmal, mit seinem derben, zuweilen gemeinen Eifer, seinen Komödiantengriffen und ärgerlichen Witzspielen zu entweihen.\"\n" +
      "\n" +
      "Das Klostergebäude (Franziskanerplatz 4) hat eine markante Fassade mit vertieften Kreisfeldern. An der Ecke zur Singerstraße befindet sich die Statue \"Christus an der Geißelsäule\". Das sogenannte Kapitelhaus wurde im 17. Jahrhundert zu einer zweischiffigen Kapelle umgestaltet (siehe unten). In der Kirche, die zwischen 1893 und 1895 grundlegend restauriert wurde, werden verschiedene Kultgegenstände verwahrt (neben \"Maria mit der Axt\" Reliquien der heiligen Filomene und ein Muttergottesbild des heiligen Aloysius)." +
      "Äußeres\n" +
      "Die schlichte schmale Westfassade besitzt einen hohen Giebel (der die Vertikale weiter betont) sowie Rund- und Spitzbogenfenster, die Südfassade ein strenges Portal aus dem beginnenden 17. Jahrhundert (um 1750 durch einen Vorbau [mit Statue des heiligen Hieronymus] verdeckt) sowie einen kleineren Turm. Am Renaissancegiebel befinden sich Statuen und Obelisken. Ganz oben wird die Heilige Dreifaltigkeit mit drei gleichen Köpfen dargestellt (1604), der Giebelgrund wird von Statuen der heiligen Clara und des heiligen Ludwig von Frankreich geziert. Der östliche hinter dem Chor situierte Turm wurde erst 1614 vollendet, ein ursprünglich vorhandenes Querschiff wieder abgetragen. Das Klostergebäude errichtete zwischen 1616 und 1621 Abraham Mall (zu abweichenden Angaben siehe Artikel Franziskanerkloster).\n" +
      "\n" +
      "Inneres\n" +
      "Einschiffiger langgestreckter Raum mit polygonalem Chorschluss und hochbarocker Ausgestaltung. Die zwischen den Kapellen eingezogenen Strebepfeiler und die Stuckrippen der Gewölbe vermitteln einen gotischen Eindruck, die übrige Stuckierung ist barock. Die Wiederverwendung spätmittelalterlicher Formen steht im Zusammenhang mit der Absicht, die Kraft mittelalterlichen Predigertums neu zu erwecken. Damit wird erstmals den Bestrebungen der Khleslschen Klosterreform Rechnung getragen. Der hinter dem Hochaltar liegende Mönchschor ist über die Sakristei zugänglich. In der Kirche befinden sich außerdem die älteste Kirchenorgel Wiens (eine Barockorgel aus dem Jahr 1643 von Johann Wöckherl mit teils gemalten, teils geschnitzten Flügeltüren) und ein bemerkenswertes Lesepult aus dem 17. Jahrhundert.";

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
      }
    })
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

  setHistoricalPlaces(places: BuildingEntity[]): void {
    this.historicalPlaces = places;
    console.log(this.historicalPlaces);
  }

  //Search field

  protected readonly control = new FormControl('');

  open = false;

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
      this.selectedPlace = item;

      this.selectedPlace = this.userService.enterHistoricNode(this.selectedPlace);

      this.detailedView = true;
      this.open = false;
    } else {
      window.open(item.url, '_blank');
      this.open = false;
    }
  }
}
