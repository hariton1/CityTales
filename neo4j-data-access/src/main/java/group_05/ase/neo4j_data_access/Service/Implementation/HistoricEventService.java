package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricEventService;
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
import java.util.Optional;

@Service
public class HistoricEventService implements IHistoricEventService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;

    private Driver driver;
    private final IWikipediaExtractorService wikipediaExtractorService;

    public HistoricEventService(IWikipediaExtractorService wikipediaExtractorService, Neo4jProperties properties) {
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



    @Override
    public HistoricEventDTO getEventById(int viennaHistoryWikiId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiEvents {viennaHistoryWikiId: $viennaHistoryWikiId}) RETURN p";
            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).single());

            Node node = record.get("p").asNode();
            ViennaHistoryWikiEventObject entity = mapNodeToEventEntity(node);
            // Todo: change
            String content = "";
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
                ViennaHistoryWikiEventObject entity = mapNodeToEventEntity(node);
                // Todo: change
                String content = "";
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
                ViennaHistoryWikiEventObject entity = mapNodeToEventEntity(linkedNode);
                // Todo: change
                String content = "";
                HistoricEventDTO dto = new HistoricEventDTO(entity, content);
                linkedEvents.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }

        return linkedEvents;
    }

    private ViennaHistoryWikiEventObject mapNodeToEventEntity(Node node) {
        ViennaHistoryWikiEventObject eventEntity = new ViennaHistoryWikiEventObject();

        eventEntity.setViennaHistoryWikiId(node.get("viennaHistoryWikiId").asInt());
        eventEntity.setName(node.get("name").asString());
        eventEntity.setUrl(node.get("url").asString());
        eventEntity.setDateFrom(Optional.ofNullable(getSafeString(node, "dateFrom")));
        eventEntity.setDateTo(Optional.ofNullable(getSafeString(node, "dateTo")));
        eventEntity.setOrganizer(Optional.ofNullable(getSafeString(node, "organizer")));
        eventEntity.setParticipantCount(Optional.ofNullable(getSafeString(node, "participantCount")));
        eventEntity.setGnd(Optional.ofNullable(getSafeString(node, "gnd")));
        eventEntity.setWikidataId(Optional.ofNullable(getSafeString(node, "wikidataId")));
        eventEntity.setResource(Optional.ofNullable(getSafeString(node, "resource")));
        eventEntity.setTypeOfEvent(Optional.ofNullable(getSafeString(node, "typeOfEvent")));
        eventEntity.setTopic(Optional.ofNullable(getSafeString(node, "topic")));
        eventEntity.setSeeAlso(Optional.ofNullable(getSafeString(node, "seeAlso")));
        if (node.containsKey("links")) {
            eventEntity.setLinks(node.get("links").asList(Value::asString));
        } else {
            eventEntity.setLinks(new ArrayList<>());
        }

        if (node.containsKey("imageUrls")) {
            eventEntity.setImageUrls(node.get("imageUrls").asList(Value::asString));
        } else {
            eventEntity.setImageUrls(new ArrayList<>());
        }

        return eventEntity;
    }

    private String getSafeString(Node node, String key) {
        return node.containsKey(key) ? node.get(key).asString() : "N/A";
    }


}
