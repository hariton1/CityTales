package group_05.ase.data_scraper.Service.events;

import group_05.ase.data_scraper.Config.Neo4jProperties;
import group_05.ase.data_scraper.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.data_scraper.Service.embeddings.QdrantService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;


@Service
public class EventRepository {

    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private final String eventTableName = "WienGeschichteWikiEvents";
    private Driver driver;
    private final QdrantService qdrantService;


    public EventRepository(Neo4jProperties properties, QdrantService qdrantService) {
        this.NEO4J_URL = properties.getUrl();
        this.NEO4J_USER = properties.getUser();
        this.NEO4J_PASSWORD = properties.getPassword();
        this.qdrantService = qdrantService;
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

    public void persistViennaHistoryWikiEventObject(ViennaHistoryWikiEventObject obj) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MERGE (e:"+eventTableName+" {viennaHistoryWikiId: coalesce($viennaHistoryWikiId, 'N/A')}) " +
                                "SET e.url = coalesce($url, 'N/A'), " +
                                "    e.name = coalesce($name, 'N/A'), " +
                                "    e.contentGerman = coalesce($contentGerman, 'N/A'), " +
                                "    e.contentEnglish = coalesce($contentEnglish, 'N/A'), " +
                                "    e.typeOfEvent = coalesce($typeOfEvent, 'N/A'), " +
                                "    e.dateFrom = coalesce($dateFrom, 'N/A'), " +
                                "    e.dateTo = coalesce($dateTo, 'N/A'), " +
                                "    e.topic = coalesce($topic, 'N/A'), " +
                                "    e.organizer = coalesce($organizer, 'N/A'), " +
                                "    e.participantCount = coalesce($participantCount, 'N/A'), " +
                                "    e.violence = $violence, " +
                                "    e.viennaHistoryWikiId = coalesce($viennaHistoryWikiId, 'N/A'), " +
                                "    e.gnd = coalesce($gnd, 'N/A'), " +
                                "    e.wikidataId = coalesce($wikidataId, 'N/A'), " +
                                "    e.seeAlso = coalesce($seeAlso, 'N/A'), " +
                                "    e.resource = coalesce($resource, 'N/A'), " +
                                "    e.links = coalesce($links, []), " +
                                "    e.imageUrls = coalesce($imageUrls, []) " +
                                "RETURN e.name",
                        parameters(
                                "viennaHistoryWikiId", obj.getViennaHistoryWikiId(),
                                "name", obj.getName(),
                                "contentGerman", obj.getContentGerman(),
                                "contentEnglish", obj.getContentEnglish(),
                                "url", obj.getUrl(),
                                "typeOfEvent", obj.getTypeOfEvent().orElse(null),
                                "dateFrom", obj.getDateFrom().orElse(null),
                                "dateTo", obj.getDateTo().orElse(null),
                                "topic", obj.getTopic().orElse(null),
                                "organizer", obj.getOrganizer().orElse(null),
                                "participantCount", obj.getParticipantCount().orElse(null),
                                "violence", obj.getViolence().orElse(null),
                                "gnd", obj.getGnd().orElse(null),
                                "wikidataId", obj.getWikidataId().orElse(null),
                                "seeAlso", obj.getSeeAlso().orElse(null),
                                "resource", obj.getResource().orElse(null),
                                "links", obj.getLinks() != null ? obj.getLinks() : new ArrayList<>(),
                                "imageUrls", obj.getImageUrls() != null ? obj.getImageUrls() : new ArrayList<>()
                        )
                );
                return result.single().get(0).asString();
            });
            System.out.println("Created or updated Event: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void persistEmbedding(float[] embedding, int id) {
        qdrantService.upsertEntry(embedding,eventTableName,id);
    }
}
