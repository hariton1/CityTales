package group_05.ase.neo4j_data_access.Service;

import group_05.ase.neo4j_data_access.Entity.HistoricalPersonEntity;
import group_05.ase.neo4j_data_access.Entity.HistoricalPlaceEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Neo4jAccessService {

    private static final String NEO4J_URL = "bolt://localhost:7687";
    private static final String NEO4J_USER = "neo4j";
    private static final String NEO4J_PASSWORD = "***REMOVED***";

    private Driver driver;

    @PostConstruct
    public void init() {
        driver = GraphDatabase.driver(
                NEO4J_URL,
                AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD)
        );
        System.out.println("Neo4j driver initialized.");
    }

    @PreDestroy
    public void close() {
        if (driver != null) {
            driver.close();
            System.out.println("Neo4j driver closed.");
        }
    }

    public HistoricalPersonEntity getPersonById(String wikiDataId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPerson {wikiDataId: $wikiDataId}) RETURN p";
            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("wikiDataId", wikiDataId)).single());

            Node node = record.get("p").asNode();
            return mapNodeToPersonEntity(node);

        } catch (NoSuchRecordException e) {
            System.err.println("HistoricPerson with ID " + wikiDataId + " not found.");
            return null;
        }
    }

    public List<HistoricalPersonEntity> getPersonsByPartialName(String partialName) {
        List<HistoricalPersonEntity> people = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPerson) " +
                    "WHERE toLower(p.name) CONTAINS toLower($name) " +
                    "RETURN p";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("name", partialName)).list()
            );

            for (Record record : records) {
                Node node = record.get("p").asNode();
                people.add(mapNodeToPersonEntity(node));
            }

        } catch (Exception e) {
            System.err.println("Error retrieving people by name: " + e.getMessage());
        }

        return people;
    }

    public List<HistoricalPersonEntity> getAllLinkedHistoricPersonsById(String wikiDataId) {
        List<HistoricalPersonEntity> linkedPersons = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPerson {wikiDataId: $wikiDataId})-[:RELATED_TO]->(linked:HistoricPerson) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("wikiDataId", wikiDataId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                linkedPersons.add(mapNodeToPersonEntity(linkedNode));
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + wikiDataId + ": " + e.getMessage());
        }

        return linkedPersons;
    }

    public List<HistoricalPlaceEntity> findHistoricalPlacesWithinRadius(double latitude, double longitude, double radius) {
        List<HistoricalPlaceEntity> places = new ArrayList<>();

        try (Session session = driver.session()) {
            // Cypher query to find all HistoricalPlace nodes within the specified radius
            String query = "MATCH (p:HistoricalPlace) " +
                    "WHERE point.distance(p.location, point({latitude: $latitude, longitude: $longitude})) <= $radius * 1000 " + // radius in meters
                    "RETURN p";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("latitude", latitude, "longitude", longitude, "radius", radius)).list()
            );

            // Mapping the results to HistoricalPlaceEntity objects
            for (Record record : records) {
                Node node = record.get("p").asNode();
                HistoricalPlaceEntity place = mapNodeToHistoricalPlaceEntity(node);
                places.add(place);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving historical places by location: " + e.getMessage());
        }

        return places;
    }


    public HistoricalPlaceEntity getPlaceById(String wikiDataId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPlace {wikiDataId: $wikiDataId}) RETURN p";

            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("wikiDataId", wikiDataId)).single());

            Node node = record.get("p").asNode();
            return mapNodeToHistoricalPlaceEntity(node);

        } catch (NoSuchRecordException e) {
            System.err.println("HistoricPlace with ID " + wikiDataId + " not found.");
            return null;
        }
    }

    public HistoricalPersonEntity mapNodeToPersonEntity(Node node) {
        HistoricalPersonEntity personEntity = new HistoricalPersonEntity();
        personEntity.setWikiDataId(node.get("wikiDataId").asString());
        personEntity.setShortDescription(node.get("shortDescription").asString());
        personEntity.setName(node.get("name").asString());
        personEntity.setWikipediaUrl(node.get("wikipediaUrl").asString());
        personEntity.setYearOfBirth(0); // for now
        personEntity.setYearOfDeath(0); // for now
        return personEntity;
    }

    private HistoricalPlaceEntity mapNodeToHistoricalPlaceEntity(Node node) {
        HistoricalPlaceEntity place = new HistoricalPlaceEntity();
        place.setWikiDataId(node.get("wikiDataId").asString());
        place.setShortDescription(node.get("shortDescription").asString());
        place.setLocation(parseGeoString(node.get("location").toString()));
        place.setWikipediaUrl(node.get("wikipediaUrl").asString());
        place.setName(node.get("name").asString());
        return place;
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
}
