package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.Entity.HistoricalPersonEntity;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
import group_05.ase.neo4j_data_access.Service.Interface.IWikipediaExtractorService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoricPersonService implements IHistoricPersonService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;

    private Driver driver;
    private final IWikipediaExtractorService wikipediaExtractorService;

    public HistoricPersonService(IWikipediaExtractorService wikipediaExtractorService, Neo4jProperties properties) {
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

    public HistoricPersonDTO getPersonById(String wikiDataId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPerson {wikiDataId: $wikiDataId}) RETURN p";
            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("wikiDataId", wikiDataId)).single());

            Node node = record.get("p").asNode();
            HistoricalPersonEntity entity = mapNodeToPersonEntity(node);
            String content = wikipediaExtractorService.getFirstParagraph(entity.getWikipediaUrl());
            return new HistoricPersonDTO(entity,content);

        } catch (NoSuchRecordException e) {
            System.err.println("HistoricPerson with ID " + wikiDataId + " not found.");
            return null;
        }
    }

    public List<HistoricPersonDTO> getPersonsByPartialName(String partialName) {
        List<HistoricPersonDTO> personDTOs = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPerson) " +
                    "WHERE toLower(p.name) CONTAINS toLower($name) " +
                    "RETURN p";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("name", partialName)).list()
            );

            for (Record record : records) {
                Node node = record.get("p").asNode();
                HistoricalPersonEntity entity = mapNodeToPersonEntity(node);
                String content = wikipediaExtractorService.getFirstParagraph(entity.getWikipediaUrl());
                HistoricPersonDTO dto = new HistoricPersonDTO(entity, content);
                personDTOs.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving people by name: " + e.getMessage());
        }

        return personDTOs;
    }

    public List<HistoricPersonDTO> getAllLinkedHistoricPersonsById(String wikiDataId) {
        List<HistoricPersonDTO> linkedPersons = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:HistoricPerson {wikiDataId: $wikiDataId})-[:RELATED_TO]->(linked:HistoricPerson) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("wikiDataId", wikiDataId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                HistoricalPersonEntity entity = mapNodeToPersonEntity(linkedNode);
                String content = wikipediaExtractorService.getFirstParagraph(entity.getWikipediaUrl());
                HistoricPersonDTO dto = new HistoricPersonDTO(entity, content);
                linkedPersons.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + wikiDataId + ": " + e.getMessage());
        }

        return linkedPersons;
    }

    private HistoricalPersonEntity mapNodeToPersonEntity(Node node) {
        HistoricalPersonEntity personEntity = new HistoricalPersonEntity();
        personEntity.setWikiDataId(node.get("wikiDataId").asString());
        personEntity.setShortDescription(node.get("shortDescription").asString());
        personEntity.setName(node.get("name").asString());
        personEntity.setWikipediaUrl(node.get("wikipediaUrl").asString());
        personEntity.setImageUrl(node.get("imageUrl").asString());
        personEntity.setYearOfBirth(0); // for now
        personEntity.setYearOfDeath(0); // for now
        personEntity.setImageUrl(node.get("imageUrl").asString());
        return personEntity;
    }
}
