import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import { UserPointsService } from '../../../user_db.services/user-points.service';
import { UserPointDto } from '../../../user_db.dto/user-point.dto';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-xp-tracker',
  imports: [CommonModule],
  templateUrl: './xp-tracker.component.html',
  styleUrl: './xp-tracker.component.scss'
})
export class XpTrackerComponent implements OnInit {

  userId: string | null = null;
  userPoints: UserPointDto[] = [];

  constructor(
    private userPointsService: UserPointsService
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
        this.userPoints = pointsList;

        console.log(this.userPoints)
      },
      error: (err) => {
        console.error('Error loading user points:', err);
      }
    });
  }
}
