package group_05.ase.data_scraper.Service.Implementation.alternative.general;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

@Service
public class ViennaHistoryWikiLinkService {
    private final String NEO4JURL = "bolt://localhost:7687";
    private final String NEO4JUSER = "neo4j";
    private final String NEO4JPW = "***REMOVED***";
    private final String buildingTableName = "WienGeschichteWikiBuildings";
    private final String personsTableName = "WienGeschichteWikiPersons";
    private final String eventTableName = "WienGeschichteWikiEvents";


    private Driver driver;

    public ViennaHistoryWikiLinkService(){
        AuthToken authToken = AuthTokens.basic(NEO4JUSER, NEO4JPW);
        try {
            driver = GraphDatabase.driver(NEO4JURL, authToken);
        } catch (Exception e) {
            System.out.println("Could not initialize db driver");
        }
    }

    public void createLinkRelationshipsFromBuildings() {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (b:" + buildingTableName + ") " +
                            "RETURN b"
            );

            while (result.hasNext()) {
                Record record = result.next();
                Node buildingNode = record.get("b").asNode();
                String buildingName = buildingNode.get("name").asString();

                List<String> links = buildingNode.get("links").asList(Value::asString);

                for (String link : links) {
                    // First try to find a building node with the given URL
                    Result linkResult = session.run(
                            "MATCH (b2:" + buildingTableName + " {url: $url}) RETURN b2",
                            parameters("url", link)
                    );

                    if (linkResult.hasNext()) {
                        // Create relationship to building node
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (b1:" + buildingTableName + " {name: $buildingName1}), " +
                                            "(b2:" + buildingTableName + " {url: $url}) " +
                                            "MERGE (b1)-[:HAS_LINK_TO]->(b2)",
                                    parameters("buildingName1", buildingName, "url", link)
                            );
                            return null;
                        });
                    } else {
                        // Otherwise, try to find a person node with the given URL
                        Result personResult = session.run(
                                "MATCH (p:" + personsTableName + " {url: $url}) RETURN p",
                                parameters("url", link)
                        );

                        if (personResult.hasNext()) {
                            // Create relationship to person node
                            session.writeTransaction(tx -> {
                                tx.run(
                                        "MATCH (b1:" + buildingTableName + " {name: $buildingName1}), " +
                                                "(p:" + personsTableName + " {url: $url}) " +
                                                "MERGE (b1)-[:HAS_LINK_TO]->(p)",
                                        parameters("buildingName1", buildingName, "url", link)
                                );
                                return null;
                            });
                        }
                    }
                }
            }

            System.out.println("Relationships for Buildings between buildings and their linked nodes (buildings or persons) created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createLinkRelationshipsFromPersons() {
        try (Session session = driver.session()) {
            // First, get all person nodes
            Result result = session.run(
                    "MATCH (p:" + personsTableName + ") " +
                            "RETURN p"
            );

            while (result.hasNext()) {
                Record record = result.next();
                Node personNode = record.get("p").asNode();
                String personName = personNode.get("name").asString();

                List<String> links = personNode.get("links").asList(Value::asString);

                for (String link : links) {
                    // First try to find a building node with the given URL
                    Result linkResult = session.run(
                            "MATCH (b2:" + buildingTableName + " {url: $url}) RETURN b2",
                            parameters("url", link)
                    );

                    if (linkResult.hasNext()) {
                        // Create relationship to building node
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (p1:" + personsTableName + " {name: $personName1}), " +
                                            "(b2:" + buildingTableName + " {url: $url}) " +
                                            "MERGE (p1)-[:HAS_LINK_TO]->(b2)",
                                    parameters("personName1", personName, "url", link)
                            );
                            return null;
                        });
                    } else {
                        // Otherwise, try to find a person node with the given URL
                        Result personResult = session.run(
                                "MATCH (p2:" + personsTableName + " {url: $url}) RETURN p2",
                                parameters("url", link)
                        );

                        if (personResult.hasNext()) {
                            // Create relationship to person node
                            session.writeTransaction(tx -> {
                                tx.run(
                                        "MATCH (p1:" + personsTableName + " {name: $personName1}), " +
                                                "(p2:" + personsTableName + " {url: $url}) " +
                                                "MERGE (p1)-[:HAS_LINK_TO]->(p2)",
                                        parameters("personName1", personName, "url", link)
                                );
                                return null;
                            });
                        }
                    }
                }
            }

            System.out.println("Relationships for Persons between persons and their linked nodes (buildings or other persons) created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createLinkRelationshipsFromEvents() {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (e:" + eventTableName + ") " +
                            "RETURN e"
            );

            while (result.hasNext()) {
                Record record = result.next();
                Node eventNode = record.get("e").asNode();
                String eventName = eventNode.get("name").asString();

                List<String> links = eventNode.get("links").asList(Value::asString);

                for (String link : links) {
                    // Try linking to building
                    Result buildingResult = session.run(
                            "MATCH (b:" + buildingTableName + " {url: $url}) RETURN b",
                            parameters("url", link)
                    );

                    if (buildingResult.hasNext()) {
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (e1:" + eventTableName + " {name: $eventName}), " +
                                            "(b:" + buildingTableName + " {url: $url}) " +
                                            "MERGE (e1)-[:HAS_LINK_TO]->(b)",
                                    parameters("eventName", eventName, "url", link)
                            );
                            return null;
                        });
                        continue;
                    }

                    // Try linking to person
                    Result personResult = session.run(
                            "MATCH (p:" + personsTableName + " {url: $url}) RETURN p",
                            parameters("url", link)
                    );

                    if (personResult.hasNext()) {
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (e1:" + eventTableName + " {name: $eventName}), " +
                                            "(p:" + personsTableName + " {url: $url}) " +
                                            "MERGE (e1)-[:HAS_LINK_TO]->(p)",
                                    parameters("eventName", eventName, "url", link)
                            );
                            return null;
                        });
                        continue;
                    }

                    // Try linking to another event
                    Result eventResult = session.run(
                            "MATCH (e2:" + eventTableName + " {url: $url}) RETURN e2",
                            parameters("url", link)
                    );

                    if (eventResult.hasNext()) {
                        session.writeTransaction(tx -> {
                            tx.run(
                                    "MATCH (e1:" + eventTableName + " {name: $eventName}), " +
                                            "(e2:" + eventTableName + " {url: $url}) " +
                                            "MERGE (e1)-[:HAS_LINK_TO]->(e2)",
                                    parameters("eventName", eventName, "url", link)
                            );
                            return null;
                        });
                    }
                }
            }

            System.out.println("Relationships for Events (to buildings, persons, or other events) created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
