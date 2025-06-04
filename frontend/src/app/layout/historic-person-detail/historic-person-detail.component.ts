import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {TuiIcon, TuiScrollbar} from "@taiga-ui/core";
import {HistoricalPersonEntity} from '../../dto/db_entity/HistoricalPersonEntity';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';
import {Router} from '@angular/router';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-historic-person-detail',
    imports: [
        NgForOf,
        NgIf,
        TuiIcon,
        TuiScrollbar
    ],
  templateUrl: './historic-person-detail.component.html',
  styleUrl: './historic-person-detail.component.scss'
})
export class HistoricPersonDetailComponent {

  constructor(private router: Router, private userService: UserService) {
  }


  @Input()
  get selectedPerson(): any {
    return this._selectedPerson;
  }
  set selectedPerson(value: any) {
    this._selectedPerson = value;
  }
  private _selectedPerson: any;

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
}
