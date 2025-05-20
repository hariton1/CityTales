import {EventEntity} from './EventEntity';
import {HistoricalPersonEntity} from './HistoricalPersonEntity';
import {HistoricalPlaceEntity} from './HistoricalPlaceEntity';

export interface HistoricalEventEntity {
  content: string;
  event: EventEntity;
  linkedEvents: HistoricalEventEntity[];
  linkedPersons: HistoricalPersonEntity[];
  linkedPlaces: HistoricalPlaceEntity[];
}
