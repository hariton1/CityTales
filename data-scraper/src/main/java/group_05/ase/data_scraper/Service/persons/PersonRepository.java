package group_05.ase.data_scraper.Service.persons;

import group_05.ase.data_scraper.Config.Neo4jProperties;
import group_05.ase.data_scraper.Entity.ViennaHistoryWikiPersonObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;


@Service
public class PersonRepository {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private final String personsTableName = "WienGeschichteWikiPersons";

    private Driver driver;

    public PersonRepository(Neo4jProperties properties){
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

    public void persistViennaHistoryWikiPersonObject(ViennaHistoryWikiPersonObject obj) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MERGE (p:" + personsTableName + " {viennaHistoryWikiId: coalesce($viennaHistoryWikiId, 'N/A')}) " +
                                "SET p.url = coalesce($url, 'N/A'), " +
                                "    p.name = coalesce($name, 'N/A'), " +
                                "    p.content = coalesce($content, 'N/A'), " +
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
                                "    p.links = coalesce($links, []), " +
                                "    p.imageUrls = coalesce($imageUrls, []) " +
                                "RETURN p.name",
                        parameters(
                                "viennaHistoryWikiId", obj.getViennaHistoryWikiId(),
                                "name", obj.getName(),
                                "content", obj.getContent(),
                                "url", obj.getUrl(),
                                "personName", obj.getPersonName().orElse(null),
                                "alternativeName", obj.getAlternativeName().orElse(null),
                                "titles", obj.getTitles().orElse(null),
                                "sex", obj.getSex().orElse(null),
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
                                "links", obj.getLinks() != null ? obj.getLinks() : new ArrayList<>(),
                                "imageUrls", obj.getImageUrls() != null ? obj.getImageUrls() : new ArrayList<>()
                        )
                );
                return result.single().get(0).asString();
            });
            // System.out.println("Created or updated Person: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
