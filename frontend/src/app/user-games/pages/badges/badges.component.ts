import {Component, OnInit} from '@angular/core';
import {UserBadgesService} from '../../../user_db.services/user-badges.service';
import {UserBadgeDTO} from '../../../user_db.dto/user-badge.dto';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-badges',
  imports: [CommonModule],
  templateUrl: './badges.component.html',
  styleUrl: './badges.component.scss'
})
export class BadgesComponent implements OnInit{

  userId: string | null = null;
  badges: UserBadgeDTO[] = [];


  constructor(private userBadgeService: UserBadgesService) {
  }

  ngOnInit() {
    this.userId = '5be46711-4f9c-468b-a6dc-0dce03f3b318';
    console.log(this.userId);

    if (this.userId) {
      this.userBadgeService.getBadgesByUserId(this.userId).subscribe({
        next: (data) => {
          this.badges = data;
          console.log(this.badges)
        },
        error: (err) => {
          console.error('Error loading badges:', err);
        }
      });
    }
  }
  retrieveUserID() {
    const stored = localStorage.getItem("user_uuid");
    if (stored) {
      this.userId = stored;
    }
  }
}
