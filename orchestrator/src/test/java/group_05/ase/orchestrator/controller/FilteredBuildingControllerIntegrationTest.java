package group_05.ase.orchestrator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.orchestrator.dto.UserInterestWithWeightDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.Neo4jContainer;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilteredBuildingControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String TEST_USER_ID = "4ab9e382-49ab-4ee2-b226-8b1e3459c5be";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.16").withoutAuthentication();
    static MockWebServer mockUserDbServer;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("neo4j.baseUrl", () -> "http://localhost:8083");
        registry.add("userdb.baseUrl", () -> mockUserDbServer.url("/").toString());
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        neo4j.start();
        mockUserDbServer = new MockWebServer();
        mockUserDbServer.start();
    }

    @AfterAll
    static void afterAll() throws Exception {
        neo4j.stop();
        mockUserDbServer.shutdown();
    }

    @BeforeEach
    void setupNeo4jData() {
        try (
                Driver driver = GraphDatabase.driver(neo4j.getBoltUrl(), AuthTokens.none());
                Session session = driver.session()
        ) {
            session.run(
                    "CREATE (b:Building {name: $name, latitude: $lat, longitude: $lon, buildingType: $type, viennaHistoryWikiId: $wikiId, url: $url})",
                    Map.of(
                            "name", "St. Stephen's Cathedral",
                            "lat", 48.2082,
                            "lon", 16.3738,
                            "type", "Religious",
                            "wikiId", "123",
                            "url", "http://example.com"
                    )
            );
        }
    }

    @AfterEach
    void cleanupNeo4jData() {
        try (
                Driver driver = GraphDatabase.driver(neo4j.getBoltUrl(), AuthTokens.none());
                Session session = driver.session()
        ) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }

    @Test
    void returnsFilteredBuildings() throws Exception {
        List<UserInterestWithWeightDTO> interests = List.of(
                new UserInterestWithWeightDTO(1, "Religious", 1.0f)
        );
        mockUserDbServer.enqueue(new MockResponse()
                .setBody(MAPPER.writeValueAsString(interests))
                .addHeader("Content-Type", "application/json"));

        String fakeJwt = "Bearer faketoken"; // Your orchestrator does not verify JWT, just forwards it

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/buildings/filtered")
                        .queryParam("userId", TEST_USER_ID)
                        .queryParam("latitude", 48.2082)
                        .queryParam("longitude", 16.3738)
                        .queryParam("radius", 1000)
                        .build())
                .header("Authorization", fakeJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("St. Stephen's Cathedral")
                .jsonPath("$[0].buildingType").isEqualTo("Religious");
    }
}
