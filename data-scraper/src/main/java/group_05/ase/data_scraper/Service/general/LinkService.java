package group_05.ase.data_scraper.Service.general;

import group_05.ase.data_scraper.Config.Neo4jProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

@Service
public class LinkService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;
    private Driver driver;
    private final String buildingTableName = "WienGeschichteWikiBuildings";
    private final String personsTableName = "WienGeschichteWikiPersons";
    private final String eventTableName = "WienGeschichteWikiEvents";

    public LinkService(Neo4jProperties properties){
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

    public void createLinkages() {
        this.createLinkRelationshipsFromX(personsTableName,eventTableName,buildingTableName);
        this.createLinkRelationshipsFromX(buildingTableName,eventTableName,personsTableName);
        this.createLinkRelationshipsFromX(personsTableName,buildingTableName,eventTableName);
    }
                                                    // event            building       person
    public void createLinkRelationshipsFromX(String fromTable, String toTable1, String toTable2) {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (e:" + fromTable + ") " +
                            "RETURN e"
            );

            while (result.hasNext()) {
                Record record = result.next();
                Node fromNode = record.get("e").asNode();
                String fromName = fromNode.get("name").asString();

                List<String> links = fromNode.get("links").asList(Value::asString);

                for (String link : links) {
                    // Try linking to table 1
                    Result toTable1Result = session.run(
                            "MATCH (b:" + toTable1 + " {url: $url}) RETURN b",
                            parameters("url", link)
                    );

                    if (toTable1Result.hasNext()) {
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (e1:" + fromTable + " {name: $eventName}), " +
                                            "(b:" + toTable1 + " {url: $url}) " +
                                            "MERGE (e1)-[:HAS_LINK_TO]->(b)",
                                    parameters("fromName", fromName, "url", link)
                            );
                            return null;
                        });
                        continue;
                    }

                    // Try linking to table 2
                    Result toTable2Result = session.run(
                            "MATCH (p:" + toTable2 + " {url: $url}) RETURN p",
                            parameters("url", link)
                    );

                    if (toTable2Result.hasNext()) {
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (e1:" + fromTable + " {name: $eventName}), " +
                                            "(p:" + toTable2 + " {url: $url}) " +
                                            "MERGE (e1)-[:HAS_LINK_TO]->(p)",
                                    parameters("fromName", fromName, "url", link)
                            );
                            return null;
                        });
                        continue;
                    }

                    // Try linking to another event
                    Result eventResult = session.run(
                            "MATCH (e2:" + fromTable + " {url: $url}) RETURN e2",
                            parameters("url", link)
                    );

                    if (eventResult.hasNext()) {
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (e1:" + fromTable + " {name: $eventName}), " +
                                            "(e2:" + fromTable + " {url: $url}) " +
                                            "MERGE (e1)-[:HAS_LINK_TO]->(e2)",
                                    parameters("fromName", fromName, "url", link)
                            );
                            return null;
                        });
                    }
                }
            }

            System.out.println("All Relationships from: " + fromTable + " created!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
