package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Entity.ManualScraping.WienGeschichteWikiObject;
import group_05.ase.data_scraper.Entity.WikiDataObject;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import static org.neo4j.driver.Values.parameters;

@Service
public class WienGeschichteWikiPersistenceService {

    private final String NEO4JURL = "bolt://localhost:7687";
    private final String NEO4JUSER = "neo4j";
    private final String NEO4JPW = "***REMOVED***";

    private final String buildingTableName = "WienGeschichteWikiBuildings";

    private Driver driver;

    public WienGeschichteWikiPersistenceService(){
        AuthToken authToken = AuthTokens.basic(NEO4JUSER, NEO4JPW);
        try {
            driver = GraphDatabase.driver(NEO4JURL, authToken);
        } catch (Exception e) {
            System.out.println("Could not initialize db driver");
        }
    }

    public void persistWienGeschichteWikiObject(WienGeschichteWikiObject obj) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MERGE (b:"+buildingTableName+" {name: coalesce($name, 'N/A')}) " +
                                "SET b.buildingType = coalesce($buildingType, 'N/A'), " +
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
                                "    b.longitude = $longitude " +
                                "RETURN b.name",
                        parameters(
                                "name", obj.getName(),
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
                                "longitude", obj.getLongitude().orElse(null)
                        )
                );
                return result.single().get(0).asString();
            });
            System.out.println("Created or updated Building: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
