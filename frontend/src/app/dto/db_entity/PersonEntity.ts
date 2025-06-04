import {EventEntity} from './EventEntity';
import {UserHistoryDto} from '../../user_db.dto/user-history.dto';
import {BuildingEntity} from './BuildingEntity';

export interface PersonEntity {
  viennaHistoryWikiId: string;
  url: string;
  name: string;
  personName: string;
  alternativeName: string;
  titles: string;
  sex: string;
  gnd: string;
  wikidataId: string;
  birthDate: string;
  birthPlace: string;
  deathDate: string;
  deathPlace: string;
  jobs: string;
  politicalLinkage: string;
  event: string;
  estate: string;
  seeAlso: string;
  resource: string;
  links: string[];
  imageUrls: string[];

  contentGerman: string;
  contentEnglish: string;

  relatedBuildings: BuildingEntity[];
  relatedPersons: PersonEntity[];
  relatedEvents: EventEntity[];

  userHistoryEntry: UserHistoryDto;
}
