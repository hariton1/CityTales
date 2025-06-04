import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {BACKEND_ADDRESS, SERVER_ADDRESS} from '../globals';
import {TourEntity} from '../dto/tour_entity/TourEntity';
import {BuildingEntity} from '../dto/db_entity/BuildingEntity';
import {TourDto} from '../dto/tour.dto';
import {TourRequestEntity} from '../dto/tour_entity/TourRequestEntity';

@Injectable({
  providedIn: 'root'
})
export class TourService {

  constructor(private httpClient: HttpClient) {
  }

  public getDurationDistanceEstimate(start_lat: number,
                                     start_lng: number,
                                     end_lat: number,
                                     end_lng: number,
                                     stops: BuildingEntity[]) {
    const body = {
      start_lat,
      start_lng,
      end_lat,
      end_lng,
      stops
    };

    console.log("Request body: " + body.start_lng);
    console.log("Request body: " + body.start_lat);
    console.log("Request body: " + body.end_lat);
    console.log("Request body: " + body.end_lng);
    console.log("Request body: " + body.stops.toString());

    return this.httpClient.post<any>(BACKEND_ADDRESS + 'api/tour/durationDistanceEstimate', body);
  }

  public createTourInDB(tour: TourDto)
  {
    return this.httpClient.post(SERVER_ADDRESS + 'tours/createTour', TourDto.ofTourDTo(tour));
  }

  public getToursForUserId(userId: string): Observable<TourEntity[]>
  {
    return this.httpClient.get<TourEntity[]>(SERVER_ADDRESS + 'tours/user/id=' + userId);
  }

  public getTourForTourId(tourId: number): Observable<TourEntity>
  {
    return this.httpClient.get<TourEntity>(SERVER_ADDRESS + 'tours/tour/id=' + tourId);
  }

  public deleteTourById(tourId: number): void {
    var response = this.httpClient.delete(SERVER_ADDRESS + 'tours/id=' + tourId);
    response.subscribe(data => console.log(data));
  }

  public updateTour(tour: TourDto): Observable<Object> {
    console.log(TourDto.ofTourDTo(tour).id)
    return this.httpClient.patch(SERVER_ADDRESS + 'tours/id=' + tour.getId(), TourDto.ofTourDTo(tour))
  }

  public createTour(tourRequest: TourRequestEntity): Observable<TourEntity[]> {
    console.log(tourRequest)
    return this.httpClient.post<TourEntity[]>(BACKEND_ADDRESS + 'api/tour/createBasedOnInterests', tourRequest);
  }


}
