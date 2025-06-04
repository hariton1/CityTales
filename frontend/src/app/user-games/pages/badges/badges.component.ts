import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import {UserBadgesService} from '../../../user_db.services/user-badges.service';
import {UserBadgeDTO} from '../../../user_db.dto/user-badge.dto';
import {CommonModule, DatePipe} from '@angular/common';
import {CombinedService} from '../../../services/combined.service';
import {Combined} from '../../../dto/db_entity/Combined';
import {UUID} from 'node:crypto';
import {HelperService} from '../../../user_db.services/helper.service';

@Component({
  selector: 'app-badges',
  imports: [
    CommonModule,
    DatePipe
  ],
  templateUrl: './badges.component.html',
  styleUrl: './badges.component.scss'
})
export class BadgesComponent implements OnInit{

  userId: UUID | null = null;
  badges: (UserBadgeDTO & { articleDetails?: Combined })[] = [];


  constructor(
    private userBadgeService: UserBadgesService,
    private combinedService: CombinedService,
    private helperService: HelperService
  ) {
  }

  ngOnInit() {
    this.retrieveUserID();
    console.log(this.userId);

    if (this.userId) {
      this.userBadgeService.getBadgesByUserId(this.userId).subscribe({
        next: (data) => {
          this.badges = data.map(badge => {
              badge.earned_at = this.helperService.sanitizeDate(badge.earned_at)!
            return badge;
          });

          this.enrichBadgesWithArticleDetails();
          console.log(this.badges)
        },
        error: (err) => {
          console.error('Error loading badges:', err);
        }
      });
    }
  }

  enrichBadgesWithArticleDetails() {
    this.badges.forEach((badge, index) => {
      this.combinedService.getInfosById(badge.article_id).subscribe({
        next: (articleDetails) => {
          this.badges[index].articleDetails = articleDetails;
        },
        error: (err) => {
          console.error(`Error loading article details for article ID ${badge.getArticleId()}:`, err);
        }
      });
    });
  }

  retrieveUserID() {
    const stored = localStorage.getItem("user_uuid") as UUID;
    if (stored) {
      this.userId = stored;
    }
  }
}
