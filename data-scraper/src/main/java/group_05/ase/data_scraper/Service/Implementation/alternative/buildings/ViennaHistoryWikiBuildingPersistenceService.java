package group_05.ase.data_scraper.Service.Implementation.alternative.buildings;

import group_05.ase.data_scraper.Entity.ManualScraping.ViennaHistoryWikiBuildingObject;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

@Service
public class ViennaHistoryWikiBuildingPersistenceService {

    private final String NEO4JURL = "bolt://localhost:7687";
    private final String NEO4JUSER = "neo4j";
    private final String NEO4JPW = "neo4jwhatevs";
    private final String buildingTableName = "WienGeschichteWikiBuildings";
    private Driver driver;

    public ViennaHistoryWikiBuildingPersistenceService(){
        AuthToken authToken = AuthTokens.basic(NEO4JUSER, NEO4JPW);
        try {
            driver = GraphDatabase.driver(NEO4JURL, authToken);
        } catch (Exception e) {
            System.out.println("Could not initialize db driver");
        }
    }

    public void persistViennaHistoryWikiBuildingObject(ViennaHistoryWikiBuildingObject obj) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MERGE (b:"+buildingTableName+" {name: coalesce($name, 'N/A')}) " +
                                "SET b.url = coalesce($url, 'N/A'), " +
                                "    b.buildingType = coalesce($buildingType, 'N/A'), " +
                                "    b.dateFrom = coalesce($dateFrom, 'N/A'), " +
                                "    b.dateTo = coalesce($dateTo, 'N/A'), " +
                                "    b.otherName = coalesce($otherName, 'N/A'), " +
                                "    b.previousName = coalesce($previousName, 'N/A'), " +
                                "    b.namedAfter = coalesce($namedAfter, 'N/A'), " +
                                "    b.entryNumber = coalesce($entryNumber, 'N/A'), " +
                                "    b.architect = coalesce($architect, 'N/A'), " +
                                "    b.famousResidents = coalesce($famousResidents, 'N/A'), " +
                                "    b.wienGeschichteWikiId = coalesce($wienGeschichteWikiId, 'N/A'), " +
                                "    b.gnd = coalesce($gnd, 'N/A'), " +
                                "    b.wikidataId = coalesce($wikidataId, 'N/A'), " +
                                "    b.seeAlso = coalesce($seeAlso, 'N/A'), " +
                                "    b.resource = coalesce($resource, 'N/A'), " +
                                "    b.latitude = $latitude, " +
                                "    b.longitude = $longitude, " +
                                "    b.links = coalesce($links, []) " +
                                "RETURN b.name",
                        parameters(
                                "name", obj.getName(),
                                "url", obj.getUrl(),
                                "buildingType", obj.getBuildingType().orElse(null),
                                "dateFrom", obj.getDateFrom().orElse(null),
                                "dateTo", obj.getDateTo().orElse(null),
                                "otherName", obj.getOtherName().orElse(null),
                                "previousName", obj.getPreviousName().orElse(null),
                                "namedAfter", obj.getNamedAfter().orElse(null),
                                "entryNumber", obj.getEntryNumber().orElse(null),
                                "architect", obj.getArchitect().orElse(null),
                                "famousResidents", obj.getFamousResidents().orElse(null),
                                "wienGeschichteWikiId", obj.getWienGeschichteWikiId().orElse(null),
                                "gnd", obj.getGnd().orElse(null),
                                "wikidataId", obj.getWikidataId().orElse(null),
                                "seeAlso", obj.getSeeAlso().orElse(null),
                                "resource", obj.getResource().orElse(null),
                                "latitude", obj.getLatitude().orElse(null),
                                "longitude", obj.getLongitude().orElse(null),
                                "links", obj.getLinks() != null ? obj.getLinks() : new ArrayList<>()
                        )
                );
                return result.single().get(0).asString();
            });
            // System.out.println("Created or updated Building: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
