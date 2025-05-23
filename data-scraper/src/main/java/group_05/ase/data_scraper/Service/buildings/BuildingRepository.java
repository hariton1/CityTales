package group_05.ase.data_scraper.Service.buildings;

import group_05.ase.data_scraper.Config.Neo4jProperties;
import group_05.ase.data_scraper.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.data_scraper.Service.embeddings.QdrantService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

@Service
public class BuildingRepository {

    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private Driver driver;
    private final String buildingTableName = "WienGeschichteWikiBuildings";
    private final QdrantService qdrantService;


    public BuildingRepository(Neo4jProperties properties, QdrantService qdrantService){
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

    public void persistViennaHistoryWikiBuildingObject(ViennaHistoryWikiBuildingObject obj) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MERGE (b:" + buildingTableName + " {viennaHistoryWikiId: $viennaHistoryWikiId}) " +
                                "SET b.url = coalesce($url, 'N/A'), " +
                                "    b.name = coalesce($name, 'N/A'), " +
                                "    b.buildingType = coalesce($buildingType, 'N/A'), " +
                                "    b.dateFrom = coalesce($dateFrom, 'N/A'), " +
                                "    b.dateTo = coalesce($dateTo, 'N/A'), " +
                                "    b.otherName = coalesce($otherName, 'N/A'), " +
                                "    b.previousName = coalesce($previousName, 'N/A'), " +
                                "    b.namedAfter = coalesce($namedAfter, 'N/A'), " +
                                "    b.entryNumber = coalesce($entryNumber, 'N/A'), " +
                                "    b.architect = coalesce($architect, 'N/A'), " +
                                "    b.famousResidents = coalesce($famousResidents, 'N/A'), " +
                                "    b.gnd = coalesce($gnd, 'N/A'), " +
                                "    b.wikidataId = coalesce($wikidataId, 'N/A'), " +
                                "    b.seeAlso = coalesce($seeAlso, 'N/A'), " +
                                "    b.resource = coalesce($resource, 'N/A'), " +
                                "    b.latitude = $latitude, " +
                                "    b.longitude = $longitude, " +
                                "    b.links = coalesce($links, []), " +
                                "    b.imageUrls = coalesce($imageUrls, []), " +
                                "    b.contentGerman = coalesce($contentGerman, 'N/A'), " +
                                "    b.contentEnglish = coalesce($contentEnglish, 'N/A') " +
                                "RETURN b.name",
                        parameters(
                                "viennaHistoryWikiId", obj.getViennaHistoryWikiId(),
                                "url", obj.getUrl(),
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
                                "gnd", obj.getGnd().orElse(null),
                                "wikidataId", obj.getWikidataId().orElse(null),
                                "seeAlso", obj.getSeeAlso().orElse(null),
                                "resource", obj.getResource().orElse(null),
                                "latitude", obj.getLatitude().orElse(null),
                                "longitude", obj.getLongitude().orElse(null),
                                "links", obj.getLinks() != null ? obj.getLinks() : new ArrayList<>(),
                                "imageUrls", obj.getImageUrls() != null ? obj.getImageUrls() : new ArrayList<>(),
                                "contentGerman", obj.getContentGerman(),
                                "contentEnglish", obj.getContentEnglish()
                        )
                );
                return result.single().get(0).asString();
            });
            System.out.println("Created or updated Building: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void persistEmbedding(float[] embedding, int id) {
        qdrantService.upsertEntry(embedding,buildingTableName,id);
    }
}
