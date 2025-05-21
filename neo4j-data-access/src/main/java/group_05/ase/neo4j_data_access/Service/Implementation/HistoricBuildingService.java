package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IEntityDescriptionCacheService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Interface.IMappingService;
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
public class HistoricBuildingService implements IHistoricBuildingService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private Driver driver;
    private final IWikipediaExtractorService wikipediaExtractorService;
    private final IMappingService mappingService;
    private final IEntityDescriptionCacheService descriptionCacheService;

    public HistoricBuildingService(IWikipediaExtractorService wikipediaExtractorService, Neo4jProperties properties, IMappingService mappingService, IEntityDescriptionCacheService descriptionCacheService) {
        this.wikipediaExtractorService = wikipediaExtractorService;

        this.NEO4J_URL = properties.getUrl();
        this.NEO4J_USER = properties.getUser();
        this.NEO4J_PASSWORD = properties.getPassword();
        this.mappingService = mappingService;
        this.descriptionCacheService = descriptionCacheService;
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
        ViennaHistoryWikiBuildingObject entity = mappingService.mapNodeToHistoricalBuildingEntity(node);

        //preliminary solution
        String content = descriptionCacheService.extractMainArticleText(entity.getUrl());
        return new HistoricBuildingDTO(entity, content);
    }



    @Override
    public List<HistoricEventDTO> getAllLinkedHistoricEventsById(int viennaHistoryWikiId) {
        List<HistoricEventDTO> linkedEvents = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiBuildings {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiEvents) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiEventObject entity = mappingService.mapNodeToEventEntity(linkedNode);

                //preliminary content fetching
                String content = descriptionCacheService.extractMainArticleText(entity.getUrl());;
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
            String query = "MATCH (p:WienGeschichteWikiBuildings {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiBuildings) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiBuildingObject entity = mappingService.mapNodeToHistoricalBuildingEntity(linkedNode);

                //preliminary content fetching
                String content = descriptionCacheService.extractMainArticleText(entity.getUrl());;
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
            String query = "MATCH (p:WienGeschichteWikiBuildings {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiPersons) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiPersonObject entity = mappingService.mapNodeToPersonEntity(linkedNode);

                //preliminary content fetching
                String content = descriptionCacheService.extractMainArticleText(entity.getUrl());;
                HistoricPersonDTO dto = new HistoricPersonDTO(entity, content);
                linkedPersons.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }
        return linkedPersons;
    }
}
