import {TourStopEntity} from './TourStopEntity';

export interface TourEntity {
  name: string,
  start_lat: number,
  start_long: number,
  end_lat: number,
  end_long: number,
  end: GeolocationPosition,
  stops: TourStopEntity[],
  distance: number,
  durationEstimate: number
}
