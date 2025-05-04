package group_05.ase.data_scraper.Controller;

import group_05.ase.data_scraper.Entity.Graph.HistoricalPersonEntity;
import group_05.ase.data_scraper.Entity.Graph.HistoricalPlaceEntity;
import group_05.ase.data_scraper.Entity.Vector.UpsertEntryRequest;
import group_05.ase.data_scraper.Service.Implementation.WikiDataObjectPersistenceService;

import group_05.ase.data_scraper.Service.Implementation.WikipediaExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/migrate")
public class MigrateVectorDBController {

    private final WikiDataObjectPersistenceService wikiDataObjectPersistenceService;
    private final WikipediaExtractor wikipediaExtractor;

    @Autowired
    private RestTemplate restTemplate;

    public MigrateVectorDBController(WikiDataObjectPersistenceService wikiDataObjectPersistenceService, WikipediaExtractor wikipediaExtractor) {
        this.wikiDataObjectPersistenceService = wikiDataObjectPersistenceService;
        this.wikipediaExtractor = wikipediaExtractor;
    }

    @PostMapping("/status")
    public String migrateToVectorDB() {
        List<HistoricalPersonEntity> people = wikiDataObjectPersistenceService.getAllPersons();
        System.out.println("People Size: " + people.size());
        for (HistoricalPersonEntity p : people) {

            UpsertEntryRequest request = new UpsertEntryRequest();

            try {
                String content = wikipediaExtractor.extractBodyContent(p.getWikipediaUrl());
                request.setContent(content);
                request.setCollectionName("historicPersons");
                request.setWikiDataId(p.getWikiDataId());

                System.out.println(content);

                restTemplate.postForEntity(
                        "http://localhost:8081/categorize/upsert",
                        request,
                        String.class
                );
            } catch (Exception e) {
                System.out.println("Error processing: " + p.getWikiDataId());
                //e.printStackTrace();
            }
        }

        List<HistoricalPlaceEntity> places = wikiDataObjectPersistenceService.getAllPlaces();
        System.out.println("Places Size: " + places.size());
        for (HistoricalPlaceEntity p : places) {

            UpsertEntryRequest request = new UpsertEntryRequest();

            try {
                String content = wikipediaExtractor.extractBodyContent(p.getWikipediaUrl());
                request.setContent(content);
                request.setCollectionName("historicPlaces");
                request.setWikiDataId(p.getWikiDataId());

                System.out.println(content);

                restTemplate.postForEntity(
                        "http://localhost:8081/categorize/upsert",
                        request,
                        String.class
                );
            } catch (Exception e) {
                System.out.println("Error processing: " + p.getWikiDataId());
                //e.printStackTrace();
            }
        }


        return "Donezo";
    }
}
