import {UserHistoryDto} from '../../user_db.dto/user-history.dto';

export interface BuildingEntity {
    viennaHistoryWikiId: string;
    url: string;
    name: string;
    buildingType: string;
    dateFrom: number;
    dateTo: number;
    otherName: string;
    previousName: string;
    namedAfter: string;
    entryNumber: string;
    architect: string;
    famousResidents: string;
    gnd: string;
    wikidataId: string;
    seeAlso: string;
    resource: string;
    latitude: number;
    longitude: number;
    links: string[];
    imageUrls: string[];
    contentGerman: string;
    contentEnglish: string;
    userHistoryEntry: UserHistoryDto;
}
