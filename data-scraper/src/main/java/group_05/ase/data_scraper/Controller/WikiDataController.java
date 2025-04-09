package group_05.ase.data_scraper.Controller;

import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.SearchResult;
import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WikipediaResponse;
import group_05.ase.data_scraper.Service.impl.PrototypeService;
import group_05.ase.data_scraper.Service.impl.CustomWikipediaRestClient;
import group_05.ase.data_scraper.Service.impl.JWikiService;
import group_05.ase.data_scraper.Service.impl.WikiDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WikiDataController {

    private final JWikiService jwikiService;
    private final WikiDataService wikiDataService;
    private final CustomWikipediaRestClient customWikipediaApiClientService;
    private final PrototypeService prototypeService;

    public WikiDataController(JWikiService jwikiService, WikiDataService wikiDataService, CustomWikipediaRestClient customWikipediaApiClientService, PrototypeService prototypeService) {
        this.jwikiService = jwikiService;
        this.wikiDataService = wikiDataService;
        this.customWikipediaApiClientService = customWikipediaApiClientService;
        this.prototypeService = prototypeService;
    }

    @GetMapping("/api/prototype")
    public void prototype() {
        prototypeService.getAllLinksFromVienna();

        // details: separate people and places
        prototypeService.getWikiDataObjectsForList();

        //links
        //prototype.getWikiDataInterconnections();
    }

    @GetMapping("/api/links")
    public Map<String, String> getLinks() {
        return jwikiService.startRecursiveSearch("Vienna");
    }

    @GetMapping("/api/customRestClient/searchOnce")
    public void searchOnce() {
        WikipediaResponse response = customWikipediaApiClientService.searchOnce("Vienna",0);
        for (SearchResult result : response.query.search) {
            System.out.println(result);
        }
    }

    @GetMapping("/api/customRestClient/searchMultiples")
    public void searchMultiples() {
        List<SearchResult> results = customWikipediaApiClientService.searchBatch("Vienna",10,0);
        for (SearchResult result : results) {
            System.out.println(result);
        }
    }
}
