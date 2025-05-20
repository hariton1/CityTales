import {PersonEntity} from './PersonEntity';
import {HistoricalPlaceEntity} from './HistoricalPlaceEntity';
import {HistoricalEventEntity} from './HistoricalEventEntity';

export interface HistoricalPersonEntity {
  content: string;
  person: PersonEntity;
  linkedEvents: HistoricalEventEntity[];
  linkedPersons: HistoricalPersonEntity[];
  linkedPlaces: HistoricalPlaceEntity[];
}
