import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TuiAlertService, TuiAppearance, TuiScrollbar} from '@taiga-ui/core';
import {TuiCardLarge} from '@taiga-ui/layout';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserService} from '../../services/user.service';


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

  private readonly alerts = inject(TuiAlertService);

  @Input() historicalPlaces: BuildingEntity[] = [];
  @Output() selectPlaceEvent: EventEmitter<BuildingEntity> = new EventEmitter<BuildingEntity>();

  constructor(private userService: UserService) {
  }

  onDetailsClick(place: BuildingEntity) {
    place = this.userService.enterHistoricNode(place);
    console.log("Building detail emitted!");
    this.selectPlaceEvent.emit(place);
  }

}
