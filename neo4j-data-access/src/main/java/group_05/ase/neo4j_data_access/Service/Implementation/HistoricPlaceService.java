package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.DTO.HistoricPlaceDTO;
import group_05.ase.neo4j_data_access.Entity.HistoricalPlaceEntity;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPlaceService;
import group_05.ase.neo4j_data_access.Service.Interface.IWikipediaExtractorService;
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
public class HistoricPlaceService implements IHistoricPlaceService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private Driver driver;
    private final IWikipediaExtractorService wikipediaExtractorService;

    public HistoricPlaceService(IWikipediaExtractorService wikipediaExtractorService, Neo4jProperties properties) {
        this.wikipediaExtractorService = wikipediaExtractorService;

        this.NEO4J_URL = properties.getUrl();
        this.NEO4J_USER = properties.getUser();
        this.NEO4J_PASSWORD = properties.getPassword();
    }

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

    public HistoricPlaceDTO getPlaceById(String wikiDataId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPlace {wikiDataId: $wikiDataId}) RETURN p";

            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("wikiDataId", wikiDataId)).single());

            Node node = record.get("p").asNode();
            return convertToDTO(node);

        } catch (NoSuchRecordException e) {
            System.err.println("HistoricPlace with ID " + wikiDataId + " not found.");
            return null;
        }
    }
    public List<HistoricPlaceDTO> getPlaceByPartialName(String partialName) {
        List<HistoricPlaceDTO> places = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPlace) " +
                    "WHERE toLower(p.name) CONTAINS toLower($name) " +
                    "RETURN p";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("name", partialName)).list()
            );

            for (Record record : records) {
                Node node = record.get("p").asNode();
                places.add(convertToDTO(node));

            }

        } catch (Exception e) {
            System.err.println("Error retrieving places by name: " + e.getMessage());
        }

        return places;
    }

    public List<HistoricPlaceDTO> findHistoricalPlacesWithinRadius(double latitude, double longitude, double radius) {
        List<HistoricPlaceDTO> places = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "WITH point({latitude: $latitude, longitude: $longitude}) AS targetPoint " +
                    "MATCH (p:HistoricPlace) " +
                    "WITH p, split(p.location, ':') AS coords, targetPoint " +
                    "WHERE size(coords) >= 2 " +
                    "WITH p, toFloat(coords[0]) AS lat, toFloat(split(coords[1], ' ')[0]) AS lon, targetPoint " +
                    "WITH p, point({latitude: lat, longitude: lon}) AS placePoint, targetPoint " +
                    "WHERE point.distance(placePoint, targetPoint) <= $radius " + // radius in meters
                    "RETURN p";

            List<org.neo4j.driver.Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("latitude", latitude, "longitude", longitude, "radius", radius)).list()
            );

            for (Record record : records) {
                Node node = record.get("p").asNode();
                places.add(convertToDTO(node));
            }

        } catch (Exception e) {
            System.err.println("Error retrieving historical places by location: " + e.getMessage());
        }

        return places;
    }

    private HistoricPlaceDTO convertToDTO(Node node) {
        HistoricalPlaceEntity entity = mapNodeToHistoricalPlaceEntity(node);
        String content = wikipediaExtractorService.getFirstParagraph(entity.getWikipediaUrl());
        return new HistoricPlaceDTO(entity, content);
    }

    private HistoricalPlaceEntity mapNodeToHistoricalPlaceEntity(Node node) {
        HistoricalPlaceEntity place = new HistoricalPlaceEntity();
        place.setWikiDataId(node.get("wikiDataId").asString());
        place.setShortDescription(node.get("shortDescription").asString());
        place.setLocation(parseGeoString(node.get("location").toString()));
        place.setWikipediaUrl(node.get("wikipediaUrl").asString());
        place.setImageUrl(node.get("imageUrl").asString());
        place.setName(node.get("name").asString());
        place.setImageUrl(node.get("imageUrl").asString());
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
