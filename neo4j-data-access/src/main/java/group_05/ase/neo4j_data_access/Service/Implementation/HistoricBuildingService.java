package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Interface.IWikipediaExtractorService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricBuildingService implements IHistoricBuildingService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private Driver driver;
    private final IWikipediaExtractorService wikipediaExtractorService;

    public HistoricBuildingService(IWikipediaExtractorService wikipediaExtractorService, Neo4jProperties properties) {
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

    public HistoricBuildingDTO getBuildingById(int viennaHistoryWikiId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiBuildings {viennaHistoryWikiId: $viennaHistoryWikiId}) RETURN p";

            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).single());

            Node node = record.get("p").asNode();
            return convertToDTO(node);

        } catch (NoSuchRecordException e) {
            System.err.println("HistoricPlace with ID " + viennaHistoryWikiId + " not found.");
            return null;
        }
    }
    public List<HistoricBuildingDTO> getBuildingByPartialName(String partialName) {
        List<HistoricBuildingDTO> places = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiBuildings) " +
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

    public List<HistoricBuildingDTO> findHistoricalBuildingWithinRadius(double latitude, double longitude, double radius) {
        List<HistoricBuildingDTO> places = new ArrayList<>();

        try (Session session = driver.session()) {
            String query =
                    "WITH point({latitude: $latitude, longitude: $longitude}) AS targetPoint " +
                            "MATCH (p:WienGeschichteWikiBuildings) " +
                            "WHERE p.latitude IS NOT NULL AND p.longitude IS NOT NULL " +
                            "WITH p, point({latitude: p.latitude, longitude: p.longitude}) AS placePoint, targetPoint " +
                            "WHERE point.distance(placePoint, targetPoint) <= $radius " +
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

    private HistoricBuildingDTO convertToDTO(Node node) {
        ViennaHistoryWikiBuildingObject entity = mapNodeToHistoricalBuildingEntity(node);

        //preliminary solution
        String content = extractMainArticleText(entity.getUrl());
        return new HistoricBuildingDTO(entity, content);
    }

    private ViennaHistoryWikiBuildingObject mapNodeToHistoricalBuildingEntity(Node node) {
        ViennaHistoryWikiBuildingObject building = new ViennaHistoryWikiBuildingObject();

        building.setViennaHistoryWikiId(node.get("viennaHistoryWikiId").asInt());
        building.setName(node.get("name").asString());
        building.setUrl(node.get("url").asString());
        building.setBuildingType(Optional.ofNullable(getSafeStringOrNull(node, "buildingType")));
        building.setDateFrom(Optional.ofNullable(getSafeStringOrNull(node, "dateFrom")));
        building.setDateTo(Optional.ofNullable(getSafeStringOrNull(node, "dateTo")));
        building.setOtherName(Optional.ofNullable(getSafeStringOrNull(node, "otherName")));
        building.setPreviousName(Optional.ofNullable(getSafeStringOrNull(node, "previousName")));
        building.setNamedAfter(Optional.ofNullable(getSafeStringOrNull(node, "namedAfter")));
        building.setEntryNumber(Optional.ofNullable(getSafeStringOrNull(node, "entryNumber")));
        building.setArchitect(Optional.ofNullable(getSafeStringOrNull(node, "architect")));
        building.setFamousResidents(Optional.ofNullable(getSafeStringOrNull(node, "famousResidents")));
        building.setGnd(Optional.ofNullable(getSafeStringOrNull(node, "gnd")));
        building.setWikidataId(Optional.ofNullable(getSafeStringOrNull(node, "wikidataId")));
        building.setSeeAlso(Optional.ofNullable(getSafeStringOrNull(node, "seeAlso")));
        building.setResource(Optional.ofNullable(getSafeStringOrNull(node, "resource")));

        if (node.containsKey("latitude") && node.containsKey("longitude")) {
            double latitude = node.get("latitude").asDouble();
            double longitude = node.get("longitude").asDouble();
            building.setLatitude(Optional.of(latitude));
            building.setLongitude(Optional.of(longitude));
        } else {
            building.setLatitude(Optional.empty());
            building.setLongitude(Optional.empty());
        }

        if (node.containsKey("links")) {
            building.setLinks(node.get("links").asList(Value::asString));
        } else {
            building.setLinks(new ArrayList<>());
        }

        if (node.containsKey("imageUrls")) {
            building.setImageUrls(node.get("imageUrls").asList(Value::asString));
        } else {
            building.setImageUrls(new ArrayList<>());
        }

        return building;
    }

    private String getSafeStringOrNull(Node node, String key) {
        return node.containsKey(key) && !node.get(key).isNull() ? node.get(key).asString() : null;
    }

    private String extractMainArticleText(String url) {
        StringBuilder textContent = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).get();

            Element contentDiv = doc.selectFirst("div.mw-parser-output");

            if (contentDiv != null) {
                Elements paragraphs = contentDiv.select("p");

                for (Element paragraph : paragraphs) {
                    String text = paragraph.text().trim();
                    if (!text.isEmpty()) {
                        textContent.append(text).append("\n\n");
                    }
                }
            } else {
                System.err.println("Main content div not found.");
            }

        } catch (IOException e) {
            System.err.println("Error fetching URL: " + e.getMessage());
        }

        String[] parts = textContent.toString().split("\n", 7);
        if (parts.length < 7) {
            return "";
        } else {
            String rest = parts[6];
            rest = rest.replaceAll("\n", "");
            return rest;
        }
    }
}
