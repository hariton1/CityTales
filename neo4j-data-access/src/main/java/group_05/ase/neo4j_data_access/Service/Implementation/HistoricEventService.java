package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricEventService;
import group_05.ase.neo4j_data_access.Service.Interface.IMappingService;
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

@Service
public class HistoricEventService implements IHistoricEventService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;

    private Driver driver;
    private final IWikipediaExtractorService wikipediaExtractorService;
    private final IMappingService mappingService;

    public HistoricEventService(IWikipediaExtractorService wikipediaExtractorService, Neo4jProperties properties, IMappingService mappingService) {
        this.wikipediaExtractorService = wikipediaExtractorService;
        this.NEO4J_URL = properties.getUrl();
        this.NEO4J_USER = properties.getUser();
        this.NEO4J_PASSWORD = properties.getPassword();
        this.mappingService = mappingService;
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



    @Override
    public HistoricEventDTO getEventById(int viennaHistoryWikiId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiEvents {viennaHistoryWikiId: $viennaHistoryWikiId}) RETURN p";
            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).single());

            Node node = record.get("p").asNode();
            ViennaHistoryWikiEventObject entity = mappingService.mapNodeToEventEntity(node);

            // preliminary content fetching
            String content = extractMainArticleText(entity.getUrl());
            return new HistoricEventDTO(entity,content);

        } catch (NoSuchRecordException e) {
            System.err.println("WienGeschichteWikiPersons with ID " + viennaHistoryWikiId + " not found.");
            return null;
        }
    }

    @Override
    public List<HistoricEventDTO> getEventByPartialName(String partialName) {
        List<HistoricEventDTO> eventDTOs = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiEvents) " +
                    "WHERE toLower(p.name) CONTAINS toLower($name) " +
                    "RETURN p";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("name", partialName)).list()
            );

            for (Record record : records) {
                Node node = record.get("p").asNode();
                ViennaHistoryWikiEventObject entity = mappingService.mapNodeToEventEntity(node);

                // preliminary content fetching
                String content = extractMainArticleText(entity.getUrl());
                HistoricEventDTO dto = new HistoricEventDTO(entity, content);
                eventDTOs.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving people by name: " + e.getMessage());
        }

        return eventDTOs;
    }

    @Override
    public List<HistoricEventDTO> getAllLinkedHistoricEventsById(int viennaHistoryWikiId) {
        List<HistoricEventDTO> linkedEvents = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiEvents {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiEvents) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiEventObject entity = mappingService.mapNodeToEventEntity(linkedNode);

                //preliminary content fetching
                String content = extractMainArticleText(entity.getUrl());;
                HistoricEventDTO dto = new HistoricEventDTO(entity, content);
                linkedEvents.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }

        return linkedEvents;
    }

    @Override
    public List<HistoricBuildingDTO> getAllLinkedHistoricBuildingsById(int viennaHistoryWikiId) {
        List<HistoricBuildingDTO> linkedBuildings = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiEvents {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiBuildings) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiBuildingObject entity = mappingService.mapNodeToHistoricalBuildingEntity(linkedNode);

                //preliminary content fetching
                String content = extractMainArticleText(entity.getUrl());;
                HistoricBuildingDTO dto = new HistoricBuildingDTO(entity, content);
                linkedBuildings.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }

        return linkedBuildings;
    }

    @Override
    public List<HistoricPersonDTO> getAllLinkedHistoricPersonsById(int viennaHistoryWikiId) {
        List<HistoricPersonDTO> linkedPersons = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiEvents {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiPersons) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiPersonObject entity = mappingService.mapNodeToPersonEntity(linkedNode);

                //preliminary content fetching
                String content = extractMainArticleText(entity.getUrl());;
                HistoricPersonDTO dto = new HistoricPersonDTO(entity, content);
                linkedPersons.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }
        return linkedPersons;
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
