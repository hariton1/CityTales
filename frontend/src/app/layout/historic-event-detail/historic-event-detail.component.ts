import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Router} from '@angular/router';
import {UserHistoriesService} from '../../user_db.services/user-histories.service';
import {NgForOf, NgIf} from '@angular/common';
import {TuiIcon, TuiScrollbar} from '@taiga-ui/core';
import {PersonEntity} from '../../dto/db_entity/PersonEntity';
import {EventEntity} from '../../dto/db_entity/EventEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {UserService} from '../../services/user.service';
import { FunFactService, FunFactCardDTO } from '../../services/fun-fact.service';

@Component({
  selector: 'app-historic-event-detail',
  imports: [
    NgForOf,
    NgIf,
    TuiIcon,
    TuiScrollbar
  ],
  templateUrl: './historic-event-detail.component.html',
  styleUrl: './historic-event-detail.component.scss'
})
export class HistoricEventDetailComponent {

  funFact: FunFactCardDTO | null = null;
  funFactSaved = false;

  constructor(private funFactService: FunFactService , private router: Router, private userService: UserService) {
  }


  @Input()
  get selectedEvent(): any {
    this.loadFunFact();
    return this._selectedEvent;

  }
  set selectedEvent(value: any) {
    this._selectedEvent = value;
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

  loadFunFact() {
    if (this._selectedEvent && (this._selectedEvent.id || this._selectedEvent.viennaHistoryWikiId)) {
      const id = this._selectedEvent.id ?? this._selectedEvent.viennaHistoryWikiId;
      this.funFactService.getEventFunFact(id).subscribe({
        next: fact => { this.funFact = fact; },
        error: () => { this.funFact = null; }
      });
    } else {
      this.funFact = null;
    }
  }

  saveFunFact() {
    if (!this.funFact) return;
    // TODO: Hier echten UserService-Aufruf einbauen!
    this.funFactSaved = true;
    setTimeout(() => this.funFactSaved = false, 2000);
  }

}
