import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import { UserPointsService } from '../../../user_db.services/user-points.service';
import { UserPointDto } from '../../../user_db.dto/user-point.dto';
import {CommonModule, DatePipe} from '@angular/common';
import {HelperService} from '../../../user_db.services/helper.service';

@Component({
  selector: 'app-xp-tracker',
  imports: [CommonModule,
  DatePipe],
  templateUrl: './xp-tracker.component.html',
  styleUrl: './xp-tracker.component.scss'
})
export class XpTrackerComponent implements OnInit {

  userId: string | null = null;
  userPoints: UserPointDto[] = [];

  constructor(
    private userPointsService: UserPointsService,
    private helperService: HelperService
  ) {}

  ngOnInit(): void {
    this.retrieveUserID();
    if (this.userId) {
      this.loadUserPoints(this.userId);
    }
  }

  retrieveUserID(): void {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }

  loadUserPoints(userId: string): void {
    this.userPointsService.getUserPointsByUserId(userId).subscribe({
      next: (pointsList) => {
        this.userPoints = pointsList.map(x =>
          new UserPointDto(
            x.user_point_id,
            x.user_id,
            x.points,
            this.helperService.sanitizeDate(x.earned_at)!,
            x.article_id
          )
        );

        console.log(this.userPoints)
      },
      error: (err) => {
        console.error('Error loading user points:', err);
      }
    });
  }
}
