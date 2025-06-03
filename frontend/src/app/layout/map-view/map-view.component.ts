import {Component, inject, OnInit, Output, ViewChild, EventEmitter} from '@angular/core';
import {GoogleMapsModule, MapInfoWindow, MapMarker} from '@angular/google-maps';
import {CommonModule} from '@angular/common';
import {UserLocationService} from '../../services/user-location.service';
import {LocationService} from '../../services/location.service';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {TuiAlertService} from '@taiga-ui/core';
import {UserService} from '../../services/user.service';

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

  constructor(private locationService: LocationService,
              private userLocationService: UserLocationService,
              private userService: UserService) {
  }

  @Output() selectPlaceEvent: EventEmitter<BuildingEntity> = new EventEmitter<BuildingEntity>();
  @Output() populatePlacesEvent = new EventEmitter<BuildingEntity[]>();
  @Output() setDetailedViewEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @ViewChild(MapInfoWindow) infoWindow: any;

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

  markers: any[] = [];
  center: google.maps.LatLngLiteral = {lat: 48.19865798950195, lng: 16.3714542388916};
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

  markerOptions: google.maps.MarkerOptions = {
    draggable: false,
    animation: google.maps.Animation.DROP,
    icon: {
      url: 'https://cdn-icons-png.flaticon.com/512/263/263633.png',
      scaledSize: new google.maps.Size(25, 25),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(17, 34),
      labelOrigin: new google.maps.Point(12, 34)
    }
  }
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

      this.locationService.getLocationsInRadius(this.center.lat, this.center.lng, 10000).subscribe(locations => {
        this.locationsNearby = locations;
        this.addMarkersToMap(locations);
        this.populatePlacesEvent.emit(locations);
      });
  }

  addMarkersToMap(locations: BuildingEntity[]): void {
    /*
    locations.forEach(location => {
      this.markers.push({lat: location.latitude, lng: location.longitude});
    })*/

    locations.forEach(location => {
      console.log('type:', location.buildingType);
      const iconUrl = this.getIconForBuildingType(location.buildingType);

      this.markers.push({
        position: { lat: location.latitude, lng: location.longitude },
        icon: {
          url: iconUrl,
          scaledSize: new google.maps.Size(28, 28),
          anchor: new google.maps.Point(12, 12),
          labelOrigin: new google.maps.Point(12, 30)
        },
        animation: google.maps.Animation.DROP,
        building: location
      });
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

  detailAction(marker: MapMarker, location: BuildingEntity): void {

    location = this.userService.enterHistoricNode(location);

    this.combinedLocations = this.getCombinedItems(location);

    this.hoveredLocation = location;
    this.infoWindow.open(marker);

    this.selectPlaceEvent.emit(location);
    this.setDetailedViewEvent.emit(true);

    this.generatePolylines(location);
  }

  closeInfoWindow() {
    this.infoWindow.close();
    this.hoveredLocation = null;
    this.polylines = [];
  }

  // Returns CSS translate(x, y) string to position each circle around the center in a circle layout
  getCirclePosition(index: number, total: number): string {
    const radius = 60;
    const angle = (2 * Math.PI / total) * index - Math.PI / 2;
    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    return `translate(${x}px, ${y}px)`;
  }

  getCombinedItems(location: any): any[] {
    const buildings = (location.relatedBuildings || []).map((item: any) => ({ ...item, type: 'building' }));
    const persons = (location.relatedPersons || []).map((item: any) => ({ ...item, type: 'person' }));
    const events = (location.relatedEvents || []).map((item: any) => ({ ...item, type: 'event' }));

    const combined = [...buildings, ...persons, ...events];
    console.log('Combined related items:', combined);
    return combined;
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

    console.log(this.polylines)
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

  onCircleClick(loc: BuildingEntity): void {
    console.log('Clicked related location:', loc);

    this.selectPlaceEvent.emit(loc);
    this.setDetailedViewEvent.emit(true);
  }
}
