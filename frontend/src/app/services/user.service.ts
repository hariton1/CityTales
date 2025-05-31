import {HttpClient} from "@angular/common/http";
import {UserAccountDto} from "../dto/user-account.dto";
import {map, Observable} from "rxjs";
import {UserInterestDto} from '../user_db.dto/user-interest.dto'
import {SERVER_ADDRESS} from "../globals";
import {SearchHistoryDTO} from "../dto/search-history.dto";
import {TourDto} from "../dto/tour.dto";
import {FunFactDto} from "../dto/fun-fact.dto";
import {GamificationDto} from "../dto/gamificationDto";
import {Injectable} from "@angular/core";
import {InterestDto} from '../user_db.dto/interest.dto';
import {UserHistoryDto} from '../user_db.dto/user-history.dto';
import {UserPointDto} from '../user_db.dto/user-point.dto';
import {UserBadgeDTO} from '../user_db.dto/user-badge.dto';
import {UserHistoriesService} from '../user_db.services/user-histories.service';
import {UserPointsService} from '../user_db.services/user-points.service';
import {UserBadgesService} from '../user_db.services/user-badges.service';

@Injectable({ providedIn: 'root' })
export class UserService {

    constructor(private httpClient: HttpClient,
                private userHistoriesService: UserHistoriesService,
                private userPointsService: UserPointsService,
                private userBadgeService: UserBadgesService) {
    }

    /** Test Connection */
    public testConnection(): Observable<string> {
        return this.httpClient.get<string>(SERVER_ADDRESS + 'test');
    }

    /** User Account */
    public createAccount(userDTO: UserAccountDto): Observable<UserAccountDto> {
        return this.httpClient.post<UserAccountDto>(SERVER_ADDRESS + 'users/create', {
            params: [],
            data: userDTO
        });
    }

    public readAccount(userId: number): Observable<UserAccountDto> {
        return this.httpClient.get<UserAccountDto>(SERVER_ADDRESS + 'users/read' + userId);
    }

    public updateAccount(userDto: UserAccountDto): Observable<UserAccountDto> {
        return this.httpClient.put<UserAccountDto>(SERVER_ADDRESS + 'users/update', {
            params: [],
            data: userDto
        });
    }

    public deleteAccount(userId: number): Observable<boolean> {
        return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'users/delete/' + userId);
    }

    /** User Interests */
    public createInterests(userInterestDto: UserInterestDto): Observable<UserInterestDto> {
        return this.httpClient.post<UserInterestDto>(SERVER_ADDRESS + 'interests/create', {
            params: [],
            data: userInterestDto
        });
    }

  public getAllInterests(): Observable<InterestDto[]> {
      return this.httpClient.get<any>(SERVER_ADDRESS + 'interests')
        .pipe(
          map(data => data.map((item: { interest_id: number; interest_name: string; description: string; }) => {
            return new InterestDto(item.interest_id, item.interest_name, item.description);
          }))
        );
  }

    public readInterests(userId: string): Observable<UserInterestDto[]> {
      return this.httpClient.get<any[]>(SERVER_ADDRESS + 'userInterests/user_id=' + userId)
        .pipe(
          map(data => data.map(item => {
            return new UserInterestDto(item.user, item.interest_id, item.cre_dat, item.interest_weight);
          }))
        );
    }

  public readInterestDetail(interestId: number): Observable<InterestDto> {
    return this.httpClient.get<any>(SERVER_ADDRESS + 'interests/id=' + interestId)
      .pipe(
        map(item => {
          // Create a proper UserInterestDto instance from the raw JSON
          return new InterestDto(item.interest_id, item.interest_name, item.description);
        })
      );
  }

    public updateInterests(userInterestDto: UserInterestDto): Observable<UserInterestDto> {
        return this.httpClient.put<UserInterestDto>(SERVER_ADDRESS + 'interests/update', {
            params: [],
            data: userInterestDto
        });
    }

    public deleteInterests(interestId: number): Observable<boolean> {
        return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'interests/delete' + interestId);
    }

    /** Search History */
    public createSearchHistory(searchHistoryDto: SearchHistoryDTO): Observable<SearchHistoryDTO> {
        return this.httpClient.post<SearchHistoryDTO>(SERVER_ADDRESS + 'search-history/create', {
            params: [],
            data: searchHistoryDto
        });
    }

    public readSearchHistory(userID: number): Observable<SearchHistoryDTO[]> {
        return this.httpClient.get<SearchHistoryDTO[]>(SERVER_ADDRESS + 'search-history/read' + userID);
    }

    public updateSearchHistory(searchHistoryDto: SearchHistoryDTO): Observable<SearchHistoryDTO> {
        return this.httpClient.put<SearchHistoryDTO>(SERVER_ADDRESS + 'search-history/update', {
            params: [],
            data: searchHistoryDto
        });
    }

    public deleteSearchHistory(searchHistory: number): Observable<boolean> {
        return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'search-history/delete' + searchHistory);
    }

    /** User Created Tours */
    public createTour(tourDto: TourDto): Observable<TourDto> {
        return this.httpClient.post<TourDto>(SERVER_ADDRESS + 'tours/create', {
            params: [],
            data: tourDto
        });
    }

    public readTour(tourId: number): Observable<TourDto> {
        return this.httpClient.get<TourDto>(SERVER_ADDRESS + 'tours/read' + tourId);
    }

    public updateTour(tourDto: TourDto): Observable<TourDto> {
        return this.httpClient.put<TourDto>(SERVER_ADDRESS + 'tours/update', {
            params: [],
            data: tourDto
        });
    }

    public deleteTour(tourId: number): Observable<boolean> {
        return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'tours/delete' + tourId);
    }

    /** User Saved Fun Facts */
    public createFunFact(funFactDto: FunFactDto): Observable<FunFactDto> {
        return this.httpClient.post<FunFactDto>(SERVER_ADDRESS + 'fun-fact/create', {
            params: [],
            data: funFactDto
        });
    }

    public readFunFact(funFactId: number): Observable<FunFactDto> {
        return this.httpClient.get<FunFactDto>(SERVER_ADDRESS + 'fun-fact/read' + funFactId);
    }

    public updateFunFact(funFactDto: FunFactDto): Observable<FunFactDto> {
        return this.httpClient.put<FunFactDto>(SERVER_ADDRESS + 'fun-fact/update', {
            params: [],
            data: funFactDto
        });
    }

    public deleteFunFact(funFactId: number): Observable<boolean> {
        return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'fun-fact/delete' + funFactId);
    }

    /** User Gamification Data */
    public createGamification(gamificationDto: GamificationDto): Observable<GamificationDto> {
        return this.httpClient.post<GamificationDto>(SERVER_ADDRESS + 'gamification/create', {
            params: [],
            data: gamificationDto
        });
    }

    public readGamification(gamificationId: number): Observable<GamificationDto> {
        return this.httpClient.get<GamificationDto>(SERVER_ADDRESS + 'gamification/read' + gamificationId);
    }

    public updateGamification(gamificationDto: GamificationDto): Observable<GamificationDto> {
        return this.httpClient.put<GamificationDto>(SERVER_ADDRESS + 'gamification/update', {
            params: [],
            data: gamificationDto
        });
    }

    public deleteGamification(gamificationId: number): Observable<boolean> {
        return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'gamification/delete' + gamificationId);
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

    return node;
  }

  public enterHistoricNodeAlert(node: any): any {
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
      2,
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
