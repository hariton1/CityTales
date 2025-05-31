import { Injectable } from '@angular/core';
import {UserHistoryDto} from '../user_db.dto/user-history.dto';
import {UserPointDto} from '../user_db.dto/user-point.dto';
import {UserBadgeDTO} from '../user_db.dto/user-badge.dto';
import {UserHistoriesService} from '../user_db.services/user-histories.service';
import {UserPointsService} from '../user_db.services/user-points.service';
import {UserBadgesService} from '../user_db.services/user-badges.service';

@Injectable({
  providedIn: 'root',
})
export class UtilitiesService {

  constructor(private userHistoriesService: UserHistoriesService,
              private userPointsService: UserPointsService,
              private userBadgeService: UserBadgesService) {
  }

  /**
   * Converts a date to a formatted string.
   * @param date - The date to format.
   * @returns The formatted date string.
   */
  formatDate(date: Date): string {
    // Format as YYYY-MM-DDTHH:mm:ss.000+00:00
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    // Format the date string exactly as required by the backend
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000+02:00`;
  }

  public enterHistoricNode(node: any): any {
    console.log(node);

    node.userHistoryEntry = new UserHistoryDto(
      -1,
      "f5599c8c-166b-495c-accc-65addfaa572b",
      Number(node.viennaHistoryWikiId),
      new Date(),
      new Date(0),
      2);

    let newUserPointsEntry = new UserPointDto(
      -1,
      "f5599c8c-166b-495c-accc-65addfaa572b",
      1,
      new Date()
    );

    let newBadgeEntry = new UserBadgeDTO(
      -1,
      "f5599c8c-166b-495c-accc-65addfaa572b",
      Number(node.viennaHistoryWikiId),
      new Date()
    );

    this.userHistoriesService.createNewUserHistory(node.userHistoryEntry).subscribe({
      next: (results) => {
        console.log('New user history entry created successfully', results);
        node.userHistoryEntry.setUserHistoryId(results.getUserHistoryId());
        /*this.alerts
          .open('Your new user history entry is saved', {label: 'Success!', appearance: 'success', autoClose: 3000})
          .subscribe();*/
      },
      error: (err) => {
        console.error('Error creating user history entry:', err);
      }
    });

    this.userPointsService.createNewPoints(newUserPointsEntry).subscribe({
      next: (results) => {
        console.log('New user points entry created successfully', results);
        /*this.alerts
          .open('Your new user history entry is saved', {label: 'Success!', appearance: 'success', autoClose: 3000})
          .subscribe();*/
      },
      error: (err) => {
        console.error('Error creating user points entry:', err);
      }
    });

    this.userBadgeService.createUserBadge(newBadgeEntry).subscribe({
      next: (results) => {
        console.log('New badge entry created successfully', results);
        /*this.alerts
          .open('Your new user history entry is saved', {label: 'Success!', appearance: 'success', autoClose: 3000})
          .subscribe();*/
      },
      error: (err) => {
        console.error('Error creating badge entry:', err);
      }
    });

    return node;
  }

}
