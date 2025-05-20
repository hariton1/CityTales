import {BuildingEntity} from './BuildingEntity';
import {HistoricalPersonEntity} from './HistoricalPersonEntity';
import {HistoricalEventEntity} from './HistoricalEventEntity';

export interface HistoricalPlaceEntity {
  content: string;
  building: BuildingEntity;
  linkedEvents: HistoricalEventEntity[];
  linkedPersons: HistoricalPersonEntity[];
  linkedPlaces: HistoricalPlaceEntity[];
}
