package group_05.ase.data_scraper.Service.Interface;

import group_05.ase.data_scraper.Entity.Json.Root;

public interface IWikiDataScraperService {
    void batchSearch(int batchSize, int iterations);
    Root getWhatLinksHere(int limit, String continueToken);
}
