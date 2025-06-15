import {
  Component,
  inject,
  OnInit,
  Output,
  ViewChild,
  EventEmitter,
  NgZone,
  ElementRef,
  HostListener
} from '@angular/core';
import {GoogleMap, GoogleMapsModule, MapInfoWindow} from '@angular/google-maps';
import {CommonModule} from '@angular/common';
import {UserLocationService} from '../../services/user-location.service';
import {LocationService} from '../../services/location.service';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {TuiAlertService} from '@taiga-ui/core';
import {UserService} from '../../services/user.service';
import { MarkerClusterer } from '@googlemaps/markerclusterer';
import {QdrantService} from '../../services/qdrant.service';
import {UserInterestsService} from '../../user_db.services/user-interests.service';
import {catchError, forkJoin, map, Observable, of, switchMap} from 'rxjs';
import {InterestsService} from '../../user_db.services/interests.service';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';

@Component({
  selector: 'app-map-view',
  imports: [
    GoogleMapsModule,
    CommonModule
  ],
  templateUrl: './map-view.component.html',
  styleUrl: './map-view.component.less'
})
export class MapViewComponent implements OnInit{

  private readonly alerts = inject(TuiAlertService);
  private interestFiltering: string | null = null;

  constructor(private locationService: LocationService,
              private userLocationService: UserLocationService,
              private userService: UserService,
              private userInterestService: UserInterestsService,
              private interestsService: InterestsService,
              private zone: NgZone,
              private qdrantService: QdrantService
              ) {
  }

  @Output() selectDetailEvent: EventEmitter<Object> = new EventEmitter<Object>();
  @Output() populatePlacesEvent = new EventEmitter<BuildingEntity[]>();

  private selectedMarker: google.maps.Marker | null = null;
  private selectedBuildingType: string | null = null;


  @ViewChild(MapInfoWindow) infoWindow: any;
  private nativeInfoWindow = new google.maps.InfoWindow();
  @ViewChild(GoogleMap) map!: GoogleMap;

  locationsNearby: BuildingEntity[] = [];
  hoveredLocation: BuildingEntity | null = null;
  combinedLocations: any[] = [];
  hoveredRelatedName: string | null = null;
  hoveredIndex: number | null = null;

  polylineOptions: google.maps.PolylineOptions = {
    strokeColor: 'blue',
    strokeOpacity: 1.0,
    strokeWeight: 2,
  };

  markers: google.maps.Marker[] = [];
  public location_marker_dict = new Map<any, google.maps.Marker>
  center: google.maps.LatLngLiteral = {lat: 48.19865798950195, lng: 16.3714542388916};
  user_location: google.maps.LatLngLiteral = {lat: 48.19865798950195, lng: 16.3714542388916};
  zoom = 15;

  polylines: google.maps.LatLngLiteral[][] = [];

  options: google.maps.MapOptions = {
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    maxZoom: 20,
    minZoom: 4,
    zoomControl: true,
    clickableIcons: true,
    streetViewControl: false,
    fullscreenControl: false,
    mapTypeControl: true,
    mapTypeControlOptions: {
      style: google.maps.MapTypeControlStyle.DROPDOWN_MENU, // or .HORIZONTAL_BAR
      position: google.maps.ControlPosition.TOP_RIGHT       // e.g., BOTTOM_LEFT, TOP_CENTER, etc.
    },
    styles: [
      {
        "featureType": "poi",
        "elementType": "labels.text",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      },
      {
        "featureType": "poi.business",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      },
      {
        "featureType": "road",
        "elementType": "labels.icon",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      },
      {
        "featureType": "transit",
        "stylers": [
          {
            "visibility": "off"
          }
        ]
      },
      {
        featureType: 'poi.medical',
        stylers: [{ visibility: 'off' }]
      },
      {
        featureType: 'poi.attraction',
        stylers: [{ visibility: 'off' }]
      },
      {
        featureType: 'poi.park',
        stylers: [{ visibility: 'off' }]
      },
      {
        featureType: 'poi.sports_complex',
        stylers: [{ visibility: 'off' }]
      },
      {
        featureType: 'poi.school',
        stylers: [{ visibility: 'off' }]
      }
    ]
  };
  locationMarkerOptions: google.maps.MarkerOptions = {
    draggable: false,
    icon: {
      url: 'assets/icons/live-location.svg',
      scaledSize: new google.maps.Size(50, 50),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(17, 34),
      labelOrigin: new google.maps.Point(12, 34)
    }
  }
  selectedMarkerOptions: google.maps.MarkerOptions = {
    draggable: false,
    icon: {
      url: 'assets/icons/selected_location_marker.svg',
      scaledSize: new google.maps.Size(50, 50),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(17, 34),
      labelOrigin: new google.maps.Point(12, 34),
    }
  }

/*  ngOnInit(): void {
    var location = this.userLocationService.getPosition();

    location.then(position => {
      this.center = {lat: position.lat, lng: position.lng};
      this.locationService.getLocationsInRadius(position.lat, position.lng, 2000).subscribe(locations => {
        this.locationsNearby = locations;
        this.addMarkersToMap(locations);
        this.populatePlacesEvent.emit(locations);
      })
    })
  }*/

  // TODO: remove later on
  // For testing in case navigator.geolocation breaks - happened to me for some reason...
  ngOnInit(): void {
      this.interestFiltering = localStorage.getItem("interest_filtering");
      this.locationService.getLocationsInRadius(this.center.lat, this.center.lng, 10000, this.interestFiltering === 'true').subscribe(locations => {
        this.locationsNearby = locations;
        this.addMarkersToMap(locations);
        this.populatePlacesEvent.emit(locations);
      });
  }

  private clusterer!: MarkerClusterer;
  private clickedOnMarker = false;

  addMarkersToMap(locations: BuildingEntity[], batchSize = 200): void {
    const batches: BuildingEntity[][] = [];
    for (let i = 0; i < locations.length; i += batchSize) {
      batches.push(locations.slice(i, i + batchSize));
    }

    const allMarkers: google.maps.Marker[] = [];

    let delay = 0;

    batches.forEach((batch, i) => {
      setTimeout(() => {
        const batchMarkers = batch.map(location => {
          const marker = new google.maps.Marker({
            position: { lat: location.latitude, lng: location.longitude },
            icon: {
              url: this.getIconForBuildingType(location.buildingType),
              scaledSize: new google.maps.Size(28, 28),
              anchor: new google.maps.Point(12, 12),
              labelOrigin: new google.maps.Point(12, 30),
            },
            map: this.map.googleMap!,
          });

          //open details when clicking on marker
          marker.addListener('click', () => {
            this.zone.run(() => {
              this.clickedOnMarker = true;
              this.detailAction(marker, location);
            })
          });
          this.location_marker_dict.set(location.viennaHistoryWikiId, marker)
          return marker;
        });

        allMarkers.push(...batchMarkers);

        // apply clustering after the final batch has been processed
        if (i === batches.length - 1) {
          setTimeout(() => {
            this.clusterer = new MarkerClusterer({
              map: this.map.googleMap!,
              markers: allMarkers,
              algorithmOptions: {
                maxZoom: 14,
              },
            });
          }, 100);
        }
      }, delay);

      delay += 300;
    });
  }

  getIconForBuildingType(type: string): string {
    switch (type?.toLowerCase()) {
      case 'museum':
        return 'assets/icons/museum.svg';
      case 'church':
        return 'assets/icons/church.svg';
      default:
        return 'assets/icons/town-hall-11.svg';
    }
  }

  @ViewChild('relatedContentToLocation', { static: false }) relatedContentToLocationRef!: ElementRef;

  private activeMarker: google.maps.Marker | null = null;

  detailAction(marker: google.maps.Marker, location: BuildingEntity): void {
    if (!this.nativeInfoWindow) {
      this.nativeInfoWindow = new google.maps.InfoWindow();
    }

    // Reset the previous active marker (if any)
    if (this.activeMarker && this.activeMarker !== marker) {
      this.activeMarker.setIcon(this.getIconForBuildingType(location.buildingType)); // or a default icon
    }

    // Set the new icon for the active marker
    marker.setIcon({
      url: 'assets/icons/town-hall-active.svg',
      scaledSize: new google.maps.Size(28, 28),
      anchor: new google.maps.Point(12, 12),
      labelOrigin: new google.maps.Point(12, 30),
    });

    this.activeMarker = marker; // store the new active marker

    location = this.userService.enterHistoricNode(location);
    if(this.interestFiltering === 'true') {
      this.getCombinedItems(location).subscribe(combined => {
        console.log('Combined items: ', combined);
        this.combinedLocations = combined;
      });
    } else {
      this.combinedLocations = this.getCombinedItemsUnfiltered(location);
    }
    this.hoveredLocation = location;

    const relatedContent = this.relatedContentToLocationRef.nativeElement;
    relatedContent.style.display = 'block';
    this.nativeInfoWindow.setContent(relatedContent);
    this.nativeInfoWindow.open(this.map.googleMap, marker);

    this.selectDetailEvent.emit(location);

    //Center map around selected marker & change icion
    this.selectMarker(marker, location);

    this.generatePolylines(location);
  }

  selectMarker(marker: google.maps.Marker, location: BuildingEntity): void {
    if (marker == null) {
      console.log("Could not select marker since the desired object has no coordinates.");
      this.unselectMarker()
      return
    }
    this.center = {lat: marker.getPosition()?.lat()!, lng: marker.getPosition()?.lng()!}
    //Reset old marker
    if (this.selectedMarker && this.selectedMarker !== marker) {
      this.selectedMarker.setIcon({
        url: this.getIconForBuildingType(this.selectedBuildingType!),
        scaledSize: new google.maps.Size(28, 28),
        anchor: new google.maps.Point(12, 12),
        labelOrigin: new google.maps.Point(12, 30),
      })
    }

    //Set new marker and flags
    marker.setOptions(this.selectedMarkerOptions);
    this.selectedMarker = marker;
    this.selectedBuildingType = location.buildingType;
  }

  unselectMarker() {
    if (this.selectedMarker != null) {
      this.selectedMarker.setIcon({
        url: this.getIconForBuildingType(this.selectedBuildingType!),
        scaledSize: new google.maps.Size(28, 28),
        anchor: new google.maps.Point(12, 12),
        labelOrigin: new google.maps.Point(12, 30),
      })
    }
    this.selectedMarker = null;
    this.selectedBuildingType = null;
  }

  closeInfoWindow() {
    this.nativeInfoWindow.close();
    this.relatedContentToLocationRef.nativeElement.style.display = 'none';
    this.hoveredLocation = null;
    this.polylines = [];
    if (this.activeMarker) {
      this.activeMarker.setIcon({
        url: 'assets/icons/town-hall-11.svg',
        scaledSize: new google.maps.Size(28, 28),
        anchor: new google.maps.Point(12, 12),
        labelOrigin: new google.maps.Point(12, 30),
      });
      this.activeMarker = null;
    }
  }

  //hide the info window when clicking outside it
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const clickedInsideInfo = this.relatedContentToLocationRef?.nativeElement.contains(event.target);
    if (!clickedInsideInfo && !this.clickedOnMarker) {
      this.closeInfoWindow();
    }
    this.clickedOnMarker = false;
  }

  // Returns CSS translate(x, y) string to position each circle around the center in a circle layout
  getCirclePosition(index: number, total: number): string {
    const radius = 60;
    const angle = (2 * Math.PI / total) * index - Math.PI / 2;
    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    return `translate(${x}px, ${y}px)`;
  }

  getCombinedItemsUnfiltered(location: any): any[] {
    const buildings = (location.relatedBuildings || []).map((item: any) => ({ ...item, type: 'building' }));
    const persons = (location.relatedPersons || []).map((item: any) => ({ ...item, type: 'person' }));
    const events = (location.relatedEvents || []).map((item: any) => ({ ...item, type: 'event' }));

    const combined = [...buildings, ...persons, ...events];
    console.log(combined)
    return combined;
  }

  getCombinedItems(location: any): Observable<any[]> {
    let interestNames: string[] = [];

    // Start the observable pipeline and return it
    return this.userInterestService.getMyInterests().pipe(
      switchMap(interests => {
        // If no interests, emit an empty array
        if (interests.length === 0) {
          return of([]);
        }

        // Map the interests into requests for their details
        const detailRequests = interests.map(interest =>
          this.interestsService.getInterestByInterestId(interest.getInterestId())
        );

        // Use forkJoin to wait for all detail requests to complete
        return forkJoin(detailRequests);
      }),
      switchMap(interestDetails => {
        // Extract names from the interest details
        interestNames = interestDetails.map(detail => detail.getInterestNameDe());
        console.log('Interest names:', interestNames);

        // Perform semantic filtering for each category
        const buildings$ = this.qdrantService.getFilteredHistoryEntities(interestNames, 'WienGeschichteWikiBuildings');
        const persons$ = this.qdrantService.getFilteredHistoryEntities(interestNames, 'WienGeschichteWikiPersons');
        const events$ = this.qdrantService.getFilteredHistoryEntities(interestNames, 'WienGeschichteWikiEvents');

        // Use forkJoin to get all filtered entities at once
        return forkJoin([buildings$, persons$, events$]);
      }),
      map(([filteredBuildings, filteredPersons, filteredEvents]) => {
        // Apply semantic filtering for each category
        const buildings = (location.relatedBuildings && location.relatedBuildings.length > 0 ? location.relatedBuildings.filter((building: BuildingEntity) =>
          filteredBuildings.some(filteredBuilding => filteredBuilding === parseInt(building.viennaHistoryWikiId))
        ) : []).map((item: any) => ({ ...item, type: 'building' }));

        const events = (location.relatedEvents && location.relatedEvents.length > 0 ? location.relatedEvents.filter((event: EventEntity) =>
          filteredEvents.some(filteredEvent => filteredEvent === event.viennaHistoryWikiId)
        ) : []).map((item: any) => ({ ...item, type: 'event' }));

        const persons = (location.relatedPersons && location.relatedPersons.length > 0 ? location.relatedPersons.filter((person: PersonEntity) =>
          filteredPersons.some(filteredPerson => filteredPerson === parseInt(person.viennaHistoryWikiId))
        ) : []).map((item: any) => ({ ...item, type: 'person' }));

        const combined = [...buildings, ...persons, ...events];

        return combined;
      }),
      catchError(err => {
        console.error('Error fetching interests or details:', err);
        // Emit error
        return of([]);
      })
    );
  }

  generatePolylines(location: BuildingEntity): void {
    this.polylines = [];

    const center = {
      lat: location.latitude,
      lng: location.longitude
    };

    this.combinedLocations.forEach(loc => {
      if (loc.latitude && loc.longitude) {
        const related = {
          lat: loc.latitude,
          lng: loc.longitude
        };

        this.polylines.push([center, related]);
      }
    });
  }

  onCircleMouseEnter(name: string, index: number): void {
    this.hoveredRelatedName = name;
    this.hoveredIndex = index;
  }

  onCircleMouseLeave(): void {
    this.hoveredRelatedName = null;
    this.hoveredIndex = null;
  }

  getHoverLabelPosition(index: number, total: number): string {
    const radius = 60;
    const angle = (2 * Math.PI / total) * index - Math.PI / 2;
    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    return `translate(${x}px, ${y}px) translate(-50%, -50%)`;
  }

  onCircleClick(loc: any): void {
    this.selectDetailEvent.emit(loc);
    this.selectMarker(this.getMarkerToLocation(loc), loc)
    this.closeInfoWindow();
  }

  getMarkerToLocation(location: any): any {
    var marker = this.location_marker_dict.get(location.viennaHistoryWikiId)
    if(!marker){
      console.warn("Could not find location marker for location object!");
      return
    }
    return marker;
  }
}
