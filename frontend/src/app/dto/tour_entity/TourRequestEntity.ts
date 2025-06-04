import {BuildingEntity} from '../db_entity/BuildingEntity';

export interface TourRequestEntity {
  userId: string,
  start_lat: number,
  start_lng: number,
  end_lat: number,
  end_lng: number,
  predefined_stops: BuildingEntity[],
  maxDistance: number,
  minDistance: number,
  maxDuration: number,
  minDuration: number,
  maxBudget: number,
  minIntermediateStops: number
}
