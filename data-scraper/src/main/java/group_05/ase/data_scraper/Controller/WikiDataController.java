package group_05.ase.data_scraper.Controller;

import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.impl.JWikiService;
import group_05.ase.data_scraper.Service.impl.WikiDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WikiDataController {

    private final JWikiService jwikiService;
    private final WikiDataService wikiDataService;

    public WikiDataController(JWikiService jwikiService, WikiDataService wikiDataService) {
        this.jwikiService = jwikiService;
        this.wikiDataService = wikiDataService;
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
}
