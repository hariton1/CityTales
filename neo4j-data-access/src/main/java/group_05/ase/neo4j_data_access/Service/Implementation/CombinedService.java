package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.Entity.CombinedObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

@Service
public class CombinedService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private Driver driver;

    private final MappingService mappingService;

    public CombinedService( Neo4jProperties properties, MappingService mappingService) {
        this.mappingService = mappingService;

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

    public CombinedObject getCombinedObjectById(int viennaHistoryWikiId) {
        try (Session session = driver.session()) {
            String query = "MATCH (n) " +
                    "WHERE (n:WienGeschichteWikiBuildings OR n:WienGeschichteWikiEvents OR n:WienGeschichteWikiPersons) " +
                    "AND n.viennaHistoryWikiId = $viennaHistoryWikiId " +
                    "RETURN n LIMIT 1";

            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).single());

            Node node = record.get("n").asNode();
            return this.mappingService.mapNodeToCombinedObject(node);

        } catch (NoSuchRecordException e) {
            System.err.println("No entry with viennaHistoryWikiId " + viennaHistoryWikiId + " found in any table.");
            return null;
        }
    }
}
