package group_05.ase.data_scraper.Old_Scraper.Controller;

import group_05.ase.data_scraper.Old_Scraper.Service.Implementation.WikiDataScraperService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WikiDataController {
    private final WikiDataScraperService customWikipediaApiClientService;

    public WikiDataController(WikiDataScraperService customWikipediaApiClientService) {
        this.customWikipediaApiClientService = customWikipediaApiClientService;
    }
}
