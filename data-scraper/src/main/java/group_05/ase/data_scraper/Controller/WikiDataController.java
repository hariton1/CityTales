package group_05.ase.data_scraper.Controller;

import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WhatLinksHereFormat.Page;
import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WhatLinksHereFormat.Root;
import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.SearchFormat.SearchResult;
import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.SearchFormat.WikipediaResponse;
import group_05.ase.data_scraper.Service.Implementation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/api/prototype/v1")
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

    @GetMapping("/api/customRestClient/prototype/v2")
    public void searchMultiples() {
        Root root = customWikipediaApiClientService.getWhatLinksHere(10);

        if (root != null && root.query != null && root.query.pages != null) {
            for (Map.Entry<String, Page> entry : root.query.pages.entrySet()) {
                Page page = entry.getValue();
                String wikidataId = page.title;
                System.out.println("Checking id: " + wikidataId);
                System.out.println(wikiDataService.extractWikiDataObjectFromEntityId(wikidataId).toString());

            }
        } else {
            System.out.println("No pages found in response.");
        }
    }
}
