import {BuildingEntity} from './db_entity/BuildingEntity';
import {TourEntity} from './tour_entity/TourEntity';

export class TourDto {
  private id: number;
  private name: string;
  private description: string;
  private start_lat: number;
  private start_lng: number;
  private end_lat: number;
  private end_lng: number;
  private stops: BuildingEntity[];
  private distance: number;
  private durationEstimate: number;
  private userId: string;

  constructor(id:number, name: string, description: string, start_lat: number, start_lng: number, end_lat: number, end_lng: number, stops: BuildingEntity[], distance: number, durationEstimate: number, userId: string) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.start_lat = start_lat;
    this.start_lng = start_lng;
    this.end_lat = end_lat;
    this.end_lng = end_lng;
    this.stops = stops;
    this.distance = distance;
    this.durationEstimate = durationEstimate;
    this.userId = userId;
  }

  public static fromTourEntity(tourEntity:TourEntity): TourDto {

    var stops: BuildingEntity[] = JSON.parse(tourEntity.stops);

    return new TourDto(
      tourEntity.id,
      tourEntity.name,
      tourEntity.description,
      tourEntity.start_lat,
      tourEntity.start_lng,
      tourEntity.end_lat,
      tourEntity.end_lng,
      stops,
      tourEntity.distance,
      tourEntity.durationEstimate,
      tourEntity.userId
    )
  }

  public static ofTourDTo(tourDto:TourDto): TourEntity {
    var entity: TourEntity = {
      id: tourDto.getId(),
      name: tourDto.getName(),
      description: tourDto.getDescription(),
      start_lat: tourDto.getStart_lat(),
      start_lng: tourDto.getStart_lng(),
      end_lat: tourDto.getEnd_lat(),
      end_lng: tourDto.getEnd_lng(),
      stops: JSON.stringify(tourDto.getStops()),
      distance: tourDto.getDistance(),
      durationEstimate: tourDto.getDurationEstimate(),
      userId: tourDto.getUserId()
    }
    return entity;

  }

  //setter and getter

  getId(): number {
    return this.id;
  }

  setId(id: number): void {
    this.id = id;
  }

  getName(): string {
    return this.name;
  }

  setName(name: string): void {
    this.name = name;
  }

  getDescription(): string {
    return this.description;
  }

  setDescription(description: string): void {
    this.description = description;
  }

  getStart_lat(): number {
    return this.start_lat;
  }

  setStart_lat(start_lat: number): void {
    this.start_lat = start_lat;
  }

  getStart_lng(): number {
    return this.start_lng;
  }

  setStart_lng(start_lng: number): void {
    this.start_lng = start_lng;
  }

  getEnd_lat(): number {
    return this.end_lat;
  }

  setEnd_lat(end_lat: number): void {
    this.end_lat = end_lat;
  }

  getEnd_lng(): number {
    return this.end_lng;
  }

  setEnd_lng(end_lng: number): void {
    this.end_lng = end_lng;
  }

  getStops(): BuildingEntity[] {
    return this.stops;
  }

  setStops(stops: BuildingEntity[]): void {
    this.stops = stops;
  }

  getDistance(): number {
    return this.distance;
  }

  setDistance(distance: number): void {
    this.distance = distance;
  }

  getDurationEstimate(): number {
    return this.durationEstimate;
  }

  setDurationEstimate(durationEstimate: number): void {
    this.durationEstimate = durationEstimate;
  }

  getUserId(): string {
    return this.userId;
  }

  setUserId(userId: string): void {
    this.userId = userId;
  }


}
