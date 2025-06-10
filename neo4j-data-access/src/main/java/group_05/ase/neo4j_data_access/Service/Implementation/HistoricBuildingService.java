package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
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
public class HistoricBuildingService implements IHistoricBuildingService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private Driver driver;
    private final IMappingService mappingService;

    public HistoricBuildingService( Neo4jProperties properties, IMappingService mappingService) {
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

    public ViennaHistoryWikiBuildingObject getBuildingById(int viennaHistoryWikiId) {
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
    public List<ViennaHistoryWikiBuildingObject> getBuildingByPartialName(String partialName) {
        List<ViennaHistoryWikiBuildingObject> places = new ArrayList<>();

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

    public List<ViennaHistoryWikiBuildingObject> findHistoricalBuildingWithinRadius(double latitude, double longitude, double radius) {
        List<ViennaHistoryWikiBuildingObject> places = new ArrayList<>();

        try (Session session = driver.session()) {
            String query =
                    "WITH point({latitude: $latitude, longitude: $longitude}) AS targetPoint " +
                            "MATCH (p:WienGeschichteWikiBuildings) " +
                            "WHERE p.latitude IS NOT NULL AND p.longitude IS NOT NULL " +
                            "WITH p, point({latitude: p.latitude, longitude: p.longitude}) AS placePoint, targetPoint " +
                            "WHERE point.distance(placePoint, targetPoint) <= $radius " +
                            "OPTIONAL MATCH (p)-[:HAS_LINK_TO]->(related:WienGeschichteWikiBuildings) " +
                            "OPTIONAL MATCH (p)-[:HAS_LINK_TO]->(person:WienGeschichteWikiPersons) " +
                            "OPTIONAL MATCH (p)-[:HAS_LINK_TO]->(event:WienGeschichteWikiEvents) " +
                            "RETURN p, collect(DISTINCT related) AS relatedBuildings, " +
                            "collect(DISTINCT person) AS relatedPersons, " +
                            "collect(DISTINCT event) AS relatedEvents";


            List<org.neo4j.driver.Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("latitude", latitude, "longitude", longitude, "radius", radius)).list()
            );

            System.out.println("checking records");
            for (Record record : records) {
                Node mainNode = record.get("p").asNode();

                ViennaHistoryWikiBuildingObject mainObj = convertToDTO(mainNode);

                List<Node> relatedNodes = record.get("relatedBuildings").asList(Value::asNode);
                List<ViennaHistoryWikiBuildingObject> related = new ArrayList<>();
                for (Node relatedNode : relatedNodes) {
                    related.add(convertToDTO(relatedNode));
                }

                List<Node> personNodes = record.get("relatedPersons").asList(Value::asNode);
                List<ViennaHistoryWikiPersonObject> relatedPersons = new ArrayList<>();
                for (Node personNode : personNodes) {
                    relatedPersons.add(mappingService.mapNodeToPersonEntity(personNode));
                }

                List<Node> eventNodes = record.get("relatedEvents").asList(Value::asNode);
                List<ViennaHistoryWikiEventObject> relatedEvents = new ArrayList<>();
                for (Node eventNode : eventNodes) {
                    relatedEvents.add(mappingService.mapNodeToEventEntity(eventNode));
                }

                mainObj.setRelatedBuildings(related);
                mainObj.setRelatedPersons(relatedPersons);
                mainObj.setRelatedEvents(relatedEvents);

                places.add(mainObj);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving historical places by location: " + e.getMessage());
        }

        return places;
    }

    private ViennaHistoryWikiBuildingObject convertToDTO(Node node) {
        ViennaHistoryWikiBuildingObject entity = mappingService.mapNodeToHistoricalBuildingEntity(node);
        return entity;
    }

    @Override
    public List<ViennaHistoryWikiEventObject> getAllLinkedHistoricEventsById(int viennaHistoryWikiId) {
        List<ViennaHistoryWikiEventObject> linkedEvents = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiBuildings {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiEvents) " +
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
            String query = "MATCH (p:WienGeschichteWikiBuildings {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiBuildings) " +
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

    @Override
    public List<ViennaHistoryWikiPersonObject> getAllLinkedHistoricPersonsById(int viennaHistoryWikiId) {
        List<ViennaHistoryWikiPersonObject> linkedPersons = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiBuildings {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiPersons) " +
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
