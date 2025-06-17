package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
import group_05.ase.neo4j_data_access.Service.Interface.IMappingService;
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
    private final IMappingService mappingService;

    public HistoricPersonService(Neo4jProperties properties, IMappingService mappingService) {
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
    }

    @PreDestroy
    public void close() {
        if (driver != null) {
            driver.close();
        }
    }

    public ViennaHistoryWikiPersonObject getPersonById(int viennaHistoryWikiId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons {viennaHistoryWikiId: $viennaHistoryWikiId}) RETURN p";
            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).single());

            Node node = record.get("p").asNode();
            ViennaHistoryWikiPersonObject entity = mappingService.mapNodeToPersonEntity(node);

            return entity;

        } catch (NoSuchRecordException e) {
            System.err.println("WienGeschichteWikiPersons with ID " + viennaHistoryWikiId + " not found.");
            return null;
        }
    }

    public List<ViennaHistoryWikiPersonObject> getPersonsByPartialName(String partialName) {
        List<ViennaHistoryWikiPersonObject> personDTOs = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons) " +
                    "WHERE toLower(p.name) CONTAINS toLower($name) " +
                    "RETURN p";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("name", partialName)).list()
            );

            for (Record record : records) {
                Node node = record.get("p").asNode();
                ViennaHistoryWikiPersonObject entity = mappingService.mapNodeToPersonEntity(node);

                personDTOs.add(entity);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving people by name: " + e.getMessage());
        }

        return personDTOs;
    }

    @Override
    public List<ViennaHistoryWikiEventObject> getAllLinkedHistoricEventsById(int viennaHistoryWikiId) {
        List<ViennaHistoryWikiEventObject> linkedEvents = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiEvents) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiEventObject entity = mappingService.mapNodeToEventEntity(linkedNode);

                linkedEvents.add(entity);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }

        return linkedEvents;
    }

    @Override
    public List<ViennaHistoryWikiBuildingObject> getAllLinkedHistoricBuildingsById(int viennaHistoryWikiId) {
        List<ViennaHistoryWikiBuildingObject> linkedBuildings = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiBuildings) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiBuildingObject entity = mappingService.mapNodeToHistoricalBuildingEntity(linkedNode);

                linkedBuildings.add(entity);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }

        return linkedBuildings;
    }

    public List<ViennaHistoryWikiPersonObject> getAllLinkedHistoricPersonsById(int viennaHistoryWikiId) {
        List<ViennaHistoryWikiPersonObject> linkedPersons = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiPersons) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiPersonObject entity = mappingService.mapNodeToPersonEntity(linkedNode);

                linkedPersons.add(entity);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }

        return linkedPersons;
    }
}
