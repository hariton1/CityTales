package group_05.ase.data_scraper.Old_Scraper.Service.Interface;

import group_05.ase.data_scraper.Entity.Json.Root;

public interface IWikiDataScraperService {
    void batchSearch(int batchSize, int iterations, String continueToken);
    Root getWhatLinksHere(int limit, String continueToken);
}
