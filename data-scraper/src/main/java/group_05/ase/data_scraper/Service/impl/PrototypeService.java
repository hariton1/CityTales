package group_05.ase.data_scraper.Service.impl;

import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.WikiDataConsts.IPrototypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PrototypeService implements IPrototypeService {

    private final JWikiService jWikiService;
    private final WikiDataService wikiDataService;
    private List<String> links_From_Vienna;
    private List<WikiDataObject> people;
    private List<WikiDataObject> places;
    private HashMap<String,String> links;

    public PrototypeService(JWikiService jWikiService, WikiDataService wikiDataService) {
        this.jWikiService = jWikiService;
        this.wikiDataService = wikiDataService;

        people = new ArrayList<>();
        places = new ArrayList<>();
        links = new HashMap<>();
    }

    public void getAllLinksFromVienna() {
        links_From_Vienna =  jWikiService.getLinkedPagesNames("Vienna");
        System.out.println("Result Count: " + links_From_Vienna.size());
    }

    public void getWikiDataObjectsForList() {
        wikiDataService.extractWikiDataObjectList(links_From_Vienna,people,places);
    }

    // z.B.: Vienna <-- List of diplomatic missions of Italy
    // Berlin <-- List of diplomatic missions of Italy
    // Vienna <-- Berlin
    // ------------------
    // then link: List of diplomatic missions of Italy --> Berlin
    public void getWikiDataInterconnections() {
        for (String name:links_From_Vienna) {
            List<String> linkedPages = jWikiService.getLinkedPagesNames(name);

            for (String item : linkedPages) {
                if (links_From_Vienna.contains(item)) {
                    links.put(item,name);
                    System.out.println("Found link:" + item + " - " + name);
                }
            }
        }
    }
}
