package group_05.ase.data_scraper.Controller;

import group_05.ase.data_scraper.Service.Implementation.WikiDataScraperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WikiDataController {
    private final WikiDataScraperService customWikipediaApiClientService;

    public WikiDataController(WikiDataScraperService customWikipediaApiClientService) {
        this.customWikipediaApiClientService = customWikipediaApiClientService;
    }

    @GetMapping("/api/prototype/batchSearch")
    public void temp() {
        // Config
        int batchSize = 10;
        int x = 1;

        customWikipediaApiClientService.batchSearch(batchSize,x);
    }
}
