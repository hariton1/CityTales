package group_05.ase.neo4j_data_access.Service;

import group_05.ase.neo4j_data_access.Entity.HistoricalPersonEntity;
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
public class HistoricPersonService {
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
}
