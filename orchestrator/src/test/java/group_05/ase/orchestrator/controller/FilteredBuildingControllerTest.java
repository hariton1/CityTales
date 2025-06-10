package group_05.ase.orchestrator.controller;

import group_05.ase.orchestrator.util.JwtTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilteredBuildingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String TEST_USER_ID = "4ab9e382-49ab-4ee2-b226-8b1e3459c5be";

    @Test
    void returnsFilteredBuildings() {
        String jwt = JwtTestHelper.generateValidToken(TEST_USER_ID);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/buildings/filtered")
                        .queryParam("userId", TEST_USER_ID)
                        .queryParam("latitude", 48.1974)
                        .queryParam("longitude", 16.2932)
                        .queryParam("radius", 1000)
                        .queryParam("interests", "Religious,Military") // <-- So übergeben
                        .build())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    System.out.println("Response: " + new String(response.getResponseBody()));
                });
    }


}
