import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {TuiButton, TuiDropdownDirective, TuiDropdownManual} from '@taiga-ui/core';
import {TuiChevron} from '@taiga-ui/kit';
import {TuiActiveZone, TuiObscured} from '@taiga-ui/cdk';
import {UserLocationService} from '../../services/user-location.service';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import {Subscription} from 'rxjs';
import {NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';

interface Notification {
  building: BuildingEntity;
  distance: number;
  date: Date;
}

@Component({
  selector: 'app-notification-inbox',
  imports: [
    TuiButton,
    TuiChevron,
    TuiDropdownDirective,
    TuiObscured,
    TuiDropdownManual,
    TuiActiveZone,
    NgIf,
    NgForOf,
    RouterLink
  ],
  templateUrl: './notification-inbox.component.html',
  styleUrl: './notification-inbox.component.scss'
})
export class NotificationInboxComponent implements OnInit{
  buildings: BuildingEntity[] = [];
  notifications: Notification[] = [];
  readonly subscription = new Subscription();

  @Output() selectPlaceEvent: EventEmitter<BuildingEntity> = new EventEmitter<BuildingEntity>();
  @Output() setDetailedViewEvent: EventEmitter<boolean> = new EventEmitter<boolean>();

  protected open = false;

  constructor(readonly userLocationService: UserLocationService) {
  }

  ngOnInit() {
    this.subscription.add(
      this.userLocationService.historicalPlaces$.subscribe(
        buildings => {
          this.buildings = buildings;
          console.log('Notification inbox received buildings:', buildings.length);

          // Create notifications for each building
          buildings.forEach(building => {
            // Calculate distance if available from userLocationService
            const distance = this.userLocationService.calculateDistanceToBuilding(building) ?? 0;

            const notification: Notification = {
              building: building,
              distance: distance,
              date: new Date()
            };

            if (!this.notifications
              .some(existingNotification =>
                existingNotification.building.viennaHistoryWikiId === notification.building.viennaHistoryWikiId)) {
              this.notifications.push(notification);
            }

            this.notifications.sort((a, b) => {
              return a.distance - b.distance;
            });

          });

        }
      )
    );
  }

  openNotification(building: BuildingEntity): void {
    this.selectPlaceEvent.emit(building);
    this.setDetailedViewEvent.emit(true);
  }

  protected onClick(): void {
    this.open = !this.open;
  }

  protected onObscured(obscured: boolean): void {
    if (obscured) {
      this.open = false;
    }
  }

  protected onActiveZone(active: boolean): void {
    this.open = active && this.open;
  }
}
