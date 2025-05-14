package group_05.ase.data_scraper.Service.Implementation.alternative.events;

import group_05.ase.data_scraper.Entity.ManualScraping.ViennaHistoryWikiEventObject;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;


@Service
public class ViennaHistoryWikiEventPersistenceService {

    private final String NEO4JURL = "bolt://localhost:7687";
    private final String NEO4JUSER = "neo4j";
    private final String NEO4JPW = "***REMOVED***";
    private final String eventTableName = "WienGeschichteWikiEvents";
    private Driver driver;

    public ViennaHistoryWikiEventPersistenceService() {
        AuthToken authToken = AuthTokens.basic(NEO4JUSER, NEO4JPW);
        try {
            driver = GraphDatabase.driver(NEO4JURL, authToken);
        } catch (Exception e) {
            System.out.println("Could not initialize db driver");
        }
    }

    public void persistViennaHistoryWikiEventObject(ViennaHistoryWikiEventObject obj) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MERGE (e:"+eventTableName+" {name: coalesce($name, 'N/A')}) " +
                                "SET e.url = coalesce($url, 'N/A'), " +
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
                                "    e.links = coalesce($links, []) " +
                                "RETURN e.name",
                        parameters(
                                "name", obj.getName(),
                                "url", obj.getUrl(),
                                "typeOfEvent", obj.getTypeOfEvent().orElse(null),
                                "dateFrom", obj.getDateFrom().orElse(null),
                                "dateTo", obj.getDateTo().orElse(null),
                                "topic", obj.getTopic().orElse(null),
                                "organizer", obj.getOrganizer().orElse(null),
                                "participantCount", obj.getParticipantCount().orElse(null),
                                "violence", obj.getViolence().orElse(null),
                                "viennaHistoryWikiId", obj.getViennaHistoryWikiId().orElse(null),
                                "gnd", obj.getGnd().orElse(null),
                                "wikidataId", obj.getWikidataId().orElse(null),
                                "seeAlso", obj.getSeeAlso().orElse(null),
                                "resource", obj.getResource().orElse(null),
                                "links", obj.getLinks() != null ? obj.getLinks() : new ArrayList<>()
                        )
                );
                return result.single().get(0).asString();
            });
            // System.out.println("Created or updated Event: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
