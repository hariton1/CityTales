package group_05.ase.data_scraper.Service;

import group_05.ase.data_scraper.Entity.WikiDataObject;

public interface IWikiDataService {
    WikiDataObject extractWikiDataObject(String name);
}
