package group_05.ase.data_scraper.Old_Scraper.Service.Implementation;

import group_05.ase.data_scraper.Old_Scraper.Entity.Graph.HistoricalPersonEntity;
import group_05.ase.data_scraper.Old_Scraper.Entity.Graph.HistoricalPlaceEntity;
import group_05.ase.data_scraper.Old_Scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Old_Scraper.Service.Interface.IWikiDataObjectPersistenceService;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

@Service
public class WikiDataObjectPersistenceService implements IWikiDataObjectPersistenceService {

    private final String NEO4JURL = "bolt://localhost:7687";
    private final String NEO4JUSER = "neo4j";
    private final String NEO4JPW = "neo4jwhatevs";

    private Driver driver;
    private WikipediaExtractor wikipediaLinkExtractor;

    public WikiDataObjectPersistenceService(WikipediaExtractor wikipediaLinkExtractor){
        AuthToken authToken = AuthTokens.basic(NEO4JUSER, NEO4JPW);
        try {
            driver = GraphDatabase.driver(NEO4JURL, authToken);
        } catch (Exception e) {
            System.out.println("Could not initialize db driver");
        }
        this.wikipediaLinkExtractor = wikipediaLinkExtractor;
    }

    @Override
    public void persistHistoricPerson(WikiDataObject wikiDataObject) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run(
                            "MERGE (a:HistoricPerson {wikiDataId: coalesce($wikiDataId, \"N/A\"), " +
                                    "name: coalesce($name, \"N/A\"), " +
                                    "shortDescription: coalesce($shortDescription, \"N/A\"), " +
                                    "wikipediaUrl: coalesce($wikipediaUrl, \"N/A\"), " +
                                    "imageUrl: coalesce($imageUrl, \"N/A\")}) " +
                                    "RETURN a.name",
                            parameters(
                                    "name", wikiDataObject.getWikiName(),
                                    "wikiDataId", wikiDataObject.getWikiDataId(),
                                    "shortDescription", wikiDataObject.getShortDescription(),
                                    "wikipediaUrl", wikiDataObject.getWikipediaUrl(),
                                    "imageUrl", wikiDataObject.getImageUrl()
                            )
                    );
                    return result.single().get(0).asString();
                }
            });
            System.out.println("Created Historic Person: " + message);
        }
    }

    @Override
    public void persistHistoricPlace(WikiDataObject wikiDataObject) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run(
                            "MERGE (a:HistoricPlace {wikiDataId: coalesce($wikiDataId, \"N/A\"), " +
                                    "name: coalesce($name, \"N/A\"), " +
                                    "shortDescription: coalesce($shortDescription, \"N/A\"), " +
                                    "location: coalesce($location, \"N/A\"), " +
                                    "wikipediaUrl: coalesce($wikipediaUrl, \"N/A\"), " +
                                    "imageUrl: coalesce($imageUrl, \"N/A\")}) " +
                                    "RETURN a.name",
                            parameters(
                                    "name", wikiDataObject.getWikiName(),
                                    "wikiDataId", wikiDataObject.getWikiDataId(),
                                    "shortDescription", wikiDataObject.getShortDescription(),
                                    "location", wikiDataObject.getLocation(),
                                    "wikipediaUrl", wikiDataObject.getWikipediaUrl(),
                                    "imageUrl", wikiDataObject.getImageUrl()
                            )
                    );
                    return result.single().get(0).asString();
                }
            });
            System.out.println("Created Historic Place: " + message);
        }
    }


    @Override
    public void persistHistoricEvent(WikiDataObject wikiDataObject) {
    }

    @Override
    public HistoricalPersonEntity getPersonByName(String name) {

        try (Session session = driver.session()){
            String query = "MATCH (p:HistoricPerson {name: $name}) RETURN p";
            Record record = session.executeRead(tx ->
                tx.run(query, Values.parameters("name", name)).single());

            Node node = record.get("p").asNode();
            HistoricalPersonEntity personEntity = new HistoricalPersonEntity();
            personEntity.setWikiDataId(node.get("wikiDataId").asString());
            personEntity.setShortDescription(node.get("shortDescription").asString());
            personEntity.setYearOfBirth(0);
            personEntity.setYearOfDeath(0);
            personEntity.setName(node.get("name").asString());

            return personEntity;

        } catch (NoSuchRecordException e) {
            System.err.println("The requested entity does not exist!");
            return null;
        }
    }

    @Override
    public HistoricalPlaceEntity getPlaceByName(String name) {
        try (Session session = driver.session()){
            String query = "MATCH (p:HistoricPlace {name: $name}) RETURN p";
            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("name", name)).single());

            Node node = record.get("p").asNode();
            HistoricalPlaceEntity placeEntity = new HistoricalPlaceEntity();
            placeEntity.setWikiDataId(node.get("wikiDataId").asString());
            placeEntity.setShortDescription(node.get("shortDescription").asString());
            placeEntity.setLocation(parseGeoString(node.get("location").toString()));

            return placeEntity;

        } catch (NoSuchRecordException e) {
            System.err.println("The requested entity does not exist!");
            return null;
        }
    }

    @Override
    public HistoricalPlaceEntity getPlaceByCoordinates(GeographicPoint2d coordinates) {
        try (Session session = driver.session()){
            String query = "MATCH (p:HistoricPlace {location: $location}) RETURN p";
            String locationString = coordinates.getLatitude() + ":" + coordinates.getLongitude() + " (Earth)";

            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("location", locationString)).single());

            Node node = record.get("p").asNode();
            HistoricalPlaceEntity placeEntity = new HistoricalPlaceEntity();
            placeEntity.setWikiDataId(node.get("wikiDataId").asString());
            placeEntity.setShortDescription(node.get("shortDescription").asString());
            placeEntity.setLocation(parseGeoString(node.get("location").toString()));

            return placeEntity;

        } catch (NoSuchRecordException e) {
            System.err.println("The requested entity does not exist!");
            return null;
        }
    }

    private GeographicPoint2d parseGeoString(String input) {

        if (input == null || !input.contains(":")) {
            throw new IllegalArgumentException("Invalid input format");
        }
        input = input.replace("\"", "");
        String[] parts = input.split(" ")[0].split(":");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Input does not contain valid latitude and longitude");
        }

        try {
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            return new GeographicPoint2d(latitude, longitude);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Latitude or Longitude is not a valid number", e);
        }
    }

    public void upsertLinkages() {
        try (Session session = driver.session()) {
            List<HistoricalPersonEntity> personList = fetchAllHistoricalPersons(session);
            List<HistoricalPlaceEntity> placesList = fetchAllHistoricalPlaces(session);

            for (HistoricalPersonEntity person : personList) {
                List<String> linkedUrls = wikipediaLinkExtractor.extractLinks(person.getWikipediaUrl());

                createPersonLinks(session, person, linkedUrls, personList, placesList);
            }

            for (HistoricalPlaceEntity place : placesList) {
                List<String> linkedUrls = wikipediaLinkExtractor.extractLinks(place.getWikipediaUrl());

                createPlaceLinks(session, place, linkedUrls, placesList, personList);
            }
        }
    }

    public List<HistoricalPersonEntity> getAllPersons() {
        try (Session session = driver.session()){
            return fetchAllHistoricalPersons(session);
        }
    }

    public List<HistoricalPlaceEntity> getAllPlaces() {
        try (Session session = driver.session()){
            return fetchAllHistoricalPlaces(session);
        }
    }

    public List<HistoricalPersonEntity> fetchAllHistoricalPersons(Session session) {
        List<HistoricalPersonEntity> people = new ArrayList<>();
        Result result = session.run("MATCH (person:HistoricPerson) RETURN person");

        while (result.hasNext()) {
            Node node = result.next().get("person").asNode();
            people.add(mapPersonNode(node));
        }
        return people;
    }

    public List<HistoricalPlaceEntity> fetchAllHistoricalPlaces(Session session) {
        List<HistoricalPlaceEntity> places = new ArrayList<>();
        Result result = session.run("MATCH (place:HistoricPlace) RETURN place");

        while (result.hasNext()) {
            Node node = result.next().get("place").asNode();
            places.add(mapPlaceNode(node));
        }
        return places;
    }

    private HistoricalPersonEntity mapPersonNode(Node node) {
        HistoricalPersonEntity person = new HistoricalPersonEntity();
        person.setWikiDataId(node.get("wikiDataId").asString());
        person.setShortDescription(node.get("shortDescription").asString());
        person.setName(node.get("name").asString());
        person.setWikipediaUrl(node.get("wikipediaUrl").asString());
        return person;
    }

    private HistoricalPlaceEntity mapPlaceNode(Node node) {
        HistoricalPlaceEntity place = new HistoricalPlaceEntity();
        place.setWikiDataId(node.get("wikiDataId").asString());
        place.setShortDescription(node.get("shortDescription").asString());
        place.setName(node.get("name").asString());
        place.setWikipediaUrl(node.get("wikipediaUrl").asString());
        place.setLocation(parseGeoString(node.get("location").toString()));
        return place;
    }

    private void createPersonLinks(Session session, HistoricalPersonEntity currentPerson,
                                   List<String> urls, List<HistoricalPersonEntity> allPersons,
                                   List<HistoricalPlaceEntity> allPlaces) {
        for (HistoricalPersonEntity otherPerson : allPersons) {
            if (!currentPerson.equals(otherPerson) && urls.contains(otherPerson.getWikipediaUrl())) {
                logMatch("person", currentPerson.getName(), otherPerson.getName(), otherPerson.getWikipediaUrl());
                mergeRelation(session, "HistoricPerson", currentPerson.getWikiDataId(), "HistoricPerson", otherPerson.getWikiDataId());
            }
        }

        for (HistoricalPlaceEntity place : allPlaces) {
            if (urls.contains(place.getWikipediaUrl())) {
                logMatch("person", currentPerson.getName(), place.getName(), place.getWikipediaUrl());
                mergeRelation(session, "HistoricPerson", currentPerson.getWikiDataId(), "HistoricPlace", place.getWikiDataId());
            }
        }
    }

    private void createPlaceLinks(Session session, HistoricalPlaceEntity currentPlace,
                                  List<String> urls, List<HistoricalPlaceEntity> allPlaces,
                                  List<HistoricalPersonEntity> allPersons) {
        for (HistoricalPlaceEntity otherPlace : allPlaces) {
            if (!currentPlace.equals(otherPlace) && urls.contains(otherPlace.getWikipediaUrl())) {
                logMatch("place", currentPlace.getName(), otherPlace.getName(), otherPlace.getWikipediaUrl());
                mergeRelation(session, "HistoricPlace", currentPlace.getWikiDataId(), "HistoricPlace", otherPlace.getWikiDataId());
            }
        }

        for (HistoricalPersonEntity person : allPersons) {
            if (urls.contains(person.getWikipediaUrl())) {
                logMatch("place", currentPlace.getName(), person.getName(), person.getWikipediaUrl());
                mergeRelation(session, "HistoricPlace", currentPlace.getWikiDataId(), "HistoricPerson", person.getWikiDataId());
            }
        }
    }

    private void mergeRelation(Session session, String fromLabel, String fromId, String toLabel, String toId) {
        String query = String.format(
                "MATCH (a:%s {wikiDataId: $from}), (b:%s {wikiDataId: $to}) " +
                        "MERGE (a)-[:RELATED_TO]->(b)", fromLabel, toLabel
        );

        session.writeTransaction(tx -> {
            tx.run(query, parameters("from", fromId, "to", toId)).consume();
            return null;
        });
    }

    private void logMatch(String entityType, String fromName, String toName, String url) {
        System.out.printf("Found matching URL for %s: %s and %s - %s%n", entityType, fromName, toName, url);
    }
}
