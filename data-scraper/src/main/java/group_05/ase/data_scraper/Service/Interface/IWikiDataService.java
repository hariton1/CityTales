package group_05.ase.data_scraper.Service.Interface;

import group_05.ase.data_scraper.Entity.WikiDataObject;

public interface IWikiDataService {
    WikiDataObject extractWikiDataObjectFromEntityId(String entityId);
}
