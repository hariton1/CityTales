import {Component, OnInit} from '@angular/core';
import {UserBadgesService} from '../../../user_db.services/user-badges.service';
import {UserBadgeDTO} from '../../../user_db.dto/user-badge.dto';
import {CommonModule} from '@angular/common';
import {CombinedService} from '../../../services/combined.service';
import {Combined} from '../../../dto/db_entity/Combined';

@Component({
  selector: 'app-badges',
  imports: [CommonModule],
  templateUrl: './badges.component.html',
  styleUrl: './badges.component.scss'
})
export class BadgesComponent implements OnInit{

  userId: string | null = null;
  badges: (UserBadgeDTO & { articleDetails?: Combined })[] = [];


  constructor(private userBadgeService: UserBadgesService,private combinedService: CombinedService) {
  }

  ngOnInit() {
    this.retrieveUserID();
    console.log(this.userId);

    if (this.userId) {
      this.userBadgeService.getBadgesByUserId(this.userId).subscribe({
        next: (data) => {
          this.badges = data.map(badge => {
            if (badge.earned_at && badge.earned_at.includes(' PM')) {
              badge.earned_at = badge.earned_at.replace(' PM', '');
            }
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
          console.error(`Error loading article details for article ID ${badge.article_id}:`, err);
        }
      });
    });
  }

  retrieveUserID() {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }
}
