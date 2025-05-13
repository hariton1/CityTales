package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Entity.ManualScraping.ViennaHistoryWikiPersonObject;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;


@Service
public class ViennaHistoryWikiPersonPersistenceService {
    private final String NEO4JURL = "bolt://localhost:7687";
    private final String NEO4JUSER = "neo4j";
    private final String NEO4JPW = "neo4jwhatevs";
    private final String personsTableName = "WienGeschichteWikiPersons";

    private Driver driver;

    public ViennaHistoryWikiPersonPersistenceService(){
        AuthToken authToken = AuthTokens.basic(NEO4JUSER, NEO4JPW);
        try {
            driver = GraphDatabase.driver(NEO4JURL, authToken);
        } catch (Exception e) {
            System.out.println("Could not initialize db driver");
        }
    }

    public void persistViennaHistoryWikiPersonObject(ViennaHistoryWikiPersonObject obj) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MERGE (p:" + personsTableName + " {name: coalesce($name, 'N/A')}) " +
                                "SET p.url = coalesce($url, 'N/A'), " +
                                "    p.personName = coalesce($personName, 'N/A'), " +
                                "    p.alternativeName = coalesce($alternativeName, 'N/A'), " +
                                "    p.titles = coalesce($titles, 'N/A'), " +
                                "    p.sex = coalesce($sex, 'N/A'), " +
                                "    p.viennaHistoryWikiId = coalesce($viennaHistoryWikiId, 'N/A'), " +
                                "    p.gnd = coalesce($gnd, 'N/A'), " +
                                "    p.wikidataId = coalesce($wikidataId, 'N/A'), " +
                                "    p.birthDate = coalesce($birthDate, 'N/A'), " +
                                "    p.birthPlace = coalesce($birthPlace, 'N/A'), " +
                                "    p.deathDate = coalesce($deathDate, 'N/A'), " +
                                "    p.deathPlace = coalesce($deathPlace, 'N/A'), " +
                                "    p.jobs = coalesce($jobs, 'N/A'), " +
                                "    p.politicalLinkage = coalesce($politicalLinkage, 'N/A'), " +
                                "    p.event = coalesce($event, 'N/A'), " +
                                "    p.estate = coalesce($estate, 0.0), " +
                                "    p.seeAlso = coalesce($seeAlso, 0.0), " +
                                "    p.resource = coalesce($resource, 0.0), " +
                                "    p.links = coalesce($links, []) " +
                                "RETURN p.name",
                        parameters(
                                "name", obj.getName(),
                                "url", obj.getUrl(),
                                "personName", obj.getPersonName().orElse(null),
                                "alternativeName", obj.getAlternativeName().orElse(null),
                                "titles", obj.getTitles().orElse(null),
                                "sex", obj.getSex().orElse(null),
                                "viennaHistoryWikiId", obj.getViennaHistoryWikiId().orElse(null),
                                "gnd", obj.getGnd().orElse(null),
                                "wikidataId", obj.getWikidataId().orElse(null),
                                "birthDate", obj.getBirthDate().orElse(null),
                                "birthPlace", obj.getBirthPlace().orElse(null),
                                "deathDate", obj.getDeathDate().orElse(null),
                                "deathPlace", obj.getDeathPlace().orElse(null),
                                "jobs", obj.getJobs().orElse(null),
                                "politicalLinkage", obj.getPoliticalLinkage().orElse(null),
                                "event", obj.getEvent().orElse(null),
                                "estate", obj.getEstate().orElse(null),
                                "seeAlso", obj.getSeeAlso().orElse(null),
                                "resource", obj.getResource().orElse(null),
                                "links", obj.getLinks() != null ? obj.getLinks() : new ArrayList<>()
                        )
                );
                return result.single().get(0).asString();
            });
            System.out.println("Created or updated Person: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
