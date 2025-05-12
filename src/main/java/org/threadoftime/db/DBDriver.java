package org.threadoftime.db;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.SessionConfig;

import java.util.Map;

public class DBDriver {

    public static void main(String... args) {

        final String dbUri = "neo4j://localhost:7687";
        final String dbUser = "";
        final String dbPassword = "";

        try (var driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
            driver.verifyConnectivity();
            System.out.println("Connection established.");

            try(var session = driver.session(SessionConfig.builder().build())) {
                session.executeWriteWithoutResult(tx -> {
                    tx.run("MERGE (p:Person {name: $name})", Map.of("name", "Peter Lustig"));
                    var result = tx.run("""
            MATCH (p:Person)
            RETURN p.name AS name
            """);
                    System.out.println(result);
                });
            }
        }
    }
}