package group_05.ase.data_scraper.Old_Scraper.Service.Interface;

import group_05.ase.data_scraper.Old_Scraper.Entity.WikiDataObject;

public interface IWikiDataService {
    WikiDataObject extractWikiDataObjectFromEntityId(String entityId);
}
