import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TuiAppearance, TuiScrollbar} from '@taiga-ui/core';
import {TuiCardLarge} from '@taiga-ui/layout';
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {UserLocationService} from '../../services/user-location.service';
import {LocationService} from '../../services/location.service';


@Component({
  selector: 'app-historic-place-preview',
  imports: [TuiAppearance,
    TuiCardLarge,
    CommonModule, TuiScrollbar
  ],
  templateUrl: './historic-place-preview.component.html',
  styleUrl: './historic-place-preview.component.scss'
})
export class HistoricPlacePreviewComponent {

  @Input() historicalPlaces: HistoricalPlaceEntity[] = [];
  @Output() selectPlaceEvent: EventEmitter<HistoricalPlaceEntity> = new EventEmitter<HistoricalPlaceEntity>();

  onDetailsClick(place: HistoricalPlaceEntity) {
    console.log(place);
    this.selectPlaceEvent.emit(place);
  }

}
