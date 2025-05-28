import {Component, inject, OnInit, Output} from '@angular/core';
import {GoogleMapsModule} from '@angular/google-maps';
import {CommonModule} from '@angular/common';
import {UserLocationService} from '../../services/user-location.service';
import {LocationService} from '../../services/location.service';
import {EventEmitter} from '@angular/core';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';
import {UserHistoryDto} from '../../user_db.dto/user-history.dto';
import {TuiAlertService} from '@taiga-ui/core';

@Component({
  selector: 'app-map-view',
  imports: [
    GoogleMapsModule,
    CommonModule
  ],
  templateUrl: './map-view.component.html',
  styleUrl: './map-view.component.scss'
})
export class MapViewComponent implements OnInit{

  private readonly alerts = inject(TuiAlertService);

  constructor(private locationService: LocationService,
              private userLocationService: UserLocationService,
              private userHistoriesService: UserHistoriesService) {
  }

  @Output() selectPlaceEvent: EventEmitter<BuildingEntity> = new EventEmitter<BuildingEntity>();
  @Output() populatePlacesEvent = new EventEmitter<BuildingEntity[]>();
  @Output() setDetailedViewEvent: EventEmitter<boolean> = new EventEmitter<boolean>();

  locationsNearby: BuildingEntity[] = [];

  markers: any[] = [];
  center: google.maps.LatLngLiteral = {lat: 48.19865798950195, lng: 16.3714542388916};
  zoom = 15;

  options: google.maps.MapOptions = {
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    maxZoom: 20,
    minZoom: 4,
    zoomControl: true,
    clickableIcons: true,
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
    animation: google.maps.Animation.BOUNCE,
    icon: {
      url: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png',
      scaledSize: new google.maps.Size(25, 25),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(17, 34),
      labelOrigin: new google.maps.Point(12, 34)
    }
  }

  /*ngOnInit(): void {
    var location = this.userLocationService.getPosition();

    location.then(position => {
      this.center = {lat: position.lat, lng: position.lng};
      this.locationService.getLocationsInRadius(position.lat, position.lng, 2000).subscribe(locations => {
        this.locationsNearby = locations;
        this.deleteAllUnwantedImageUrls();
        this.addMarkersToMap(locations);
        this.locationsNearby.forEach(location => {this.addAllRelatedEntities(location);});
        this.populatePlacesEvent.emit(locations);
      })
    })
  }*/

  // TODO: remove later on
  // For testing in case navigator.geolocation breaks - happened to me for some reason...
  ngOnInit(): void {

      this.locationService.getLocationsInRadius(this.center.lat, this.center.lng, 100000).subscribe(locations => {
        this.locationsNearby = locations;
        /*this.deleteAllUnwantedImageUrls();*/
        this.addMarkersToMap(locations);
        /*this.locationsNearby.forEach(location => {this.addAllRelatedEntities(location);});*/
        this.populatePlacesEvent.emit(locations);
      });
  }

  addMarkersToMap(locations: BuildingEntity[]): void {
    locations.forEach(location => {
      this.markers.push({lat: location.latitude, lng: location.longitude});
    })
  }

  openMarkerInfo(location: BuildingEntity): void {
    location.userHistoryEntry = new UserHistoryDto(
      -1,
      "f5599c8c-166b-495c-accc-65addfaa572b",
      Number(location.viennaHistoryWikiId),
      new Date(),
      new Date(0),
      2);

    this.userHistoriesService.createNewUserHistory(location.userHistoryEntry).subscribe({
      next: (results) => {
        console.log('New user history entry created successfully', results);
        location.userHistoryEntry.setUserHistoryId(results.getUserHistoryId());
        this.alerts
          .open('Your new user history entry is saved', {label: 'Success!', appearance: 'success', autoClose: 3000})
          .subscribe();
      },
      error: (err) => {
        console.error('Error creating user history entry:', err);
      }
    });

    this.selectPlaceEvent.emit(location);
    this.setDetailedViewEvent.emit(true);
  }

  /*deleteAllUnwantedImageUrls(): void {
    this.locationsNearby.forEach(location => {
      location.building.imageUrls = location.building.imageUrls.filter(imageUrl => {
        return !(
          imageUrl.includes('wgw_logo_10') ||
          imageUrl.includes('RDF') ||
          imageUrl.includes('https://www.geschichtewiki.wien.gv.at/KnowledgeWiki.png') ||
          imageUrl.includes('logo_footer')
        );
      });
    });
  }*/

  /*addAllRelatedEntities(location: BuildingEntity){
    this.locationService.getLinkedLocations(location.viennaHistoryWikiId).subscribe(locations => {
      location. = locations;
    });

    this.locationService.getLinkedPersons(location.building.viennaHistoryWikiId).subscribe(persons => {
      location.linkedPersons = persons;
    });

    this.locationService.getLinkedEvents(location.building.viennaHistoryWikiId).subscribe(events => {
      location.linkedEvents = events;
    });
  }*/
}
