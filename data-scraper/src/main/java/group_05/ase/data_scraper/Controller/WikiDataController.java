package group_05.ase.data_scraper.Controller;

import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.SearchResult;
import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WikipediaResponse;
import group_05.ase.data_scraper.Entity.WikiDataObject;
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

    public WikiDataController(JWikiService jwikiService, WikiDataService wikiDataService, CustomWikipediaRestClient customWikipediaApiClientService) {
        this.jwikiService = jwikiService;
        this.wikiDataService = wikiDataService;
        this.customWikipediaApiClientService = customWikipediaApiClientService;
    }

    @GetMapping("/api/basicTest")
    public String basicTest() {
        List<String> categories = jwikiService.getCategories("Vienna");
        for (String s:categories) {
            System.out.println(s);
        }
        return jwikiService.setUp();
    }

    @GetMapping("/api/allVienna")
    public String getAllEventsRelatedToVienna() {
        jwikiService.getPageNames();
        return jwikiService.setUp();
    }

    @GetMapping("/api/run")
    public void run() {
        List<String> relatedPageNames = jwikiService.getPageNames();

        for (String pageName:relatedPageNames) {
            WikiDataObject wikiDO = wikiDataService.extractWikiDataObject(pageName);
            System.out.println(wikiDO.toString());
        }
    }

    @GetMapping("/api/links")
    public Map<String, String> getLinks() {
        return jwikiService.startRecursiveSearch("Vienna");
    }

    @GetMapping("/api/analytics")
    public void analytics() {

        List<WikiDataObject> list = wikiDataService.findPlaces();
        for (WikiDataObject wikiDataObject:list) {
            System.out.println(wikiDataObject.toString());
        }
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
