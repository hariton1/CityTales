import {PersonEntity} from './PersonEntity';
import {UserHistoryDto} from '../../user_db.dto/user-history.dto';
import {BuildingEntity} from './BuildingEntity';

export interface EventEntity {
  viennaHistoryWikiId: number;
  url: string;
  name: string;

  typeOfEvent: string;
  dateFrom: string ;
  dateTo: string;
  topic: string;
  organizer: string;
  participantCount :number;
  violence: boolean;
  gnd :string;
  wikidataId :string;
  seeAlso :string;
  resource :string;

  links :string[];
  imageUrls :string[];

  contentGerman: string;
  contentEnglish: string;

  relatedBuildings: BuildingEntity[];
  relatedPersons: PersonEntity[];
  relatedEvents: EventEntity[];

  userHistoryEntry: UserHistoryDto;
}
