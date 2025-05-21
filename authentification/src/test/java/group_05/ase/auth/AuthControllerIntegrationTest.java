package group_05.ase.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserRepository userRepository;
    private final List<String> createdEmails = new ArrayList<>();

    @AfterEach
    void cleanupAfter() {
        for (String email : createdEmails) {
            userRepository.findByEmail(email).ifPresent(userRepository::delete);
        }
        createdEmails.clear();
    }

    @Test
    void shouldRegisterAndLoginAndRefreshAndLogout() throws Exception {
        String email = "integration-" + UUID.randomUUID() + "@test.com";
        String password = "pw123";
        createdEmails.add(email);

        // 1. Register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

        // 2. Login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

        // 3. Refresh (Demo: normalerweise brauchst du hier einen echten Refresh-Token aus der App)
        // Hier demonstrativ ein Fehlerfall:
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"invalid-token\"}")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());

        // 4. Logout (Demo: Annahme, du brauchst einen Token im Body, hier Error weil dummy)
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"invalid-token\"}")
                        .with(csrf()))
                .andExpect(status().isOk()); // oder .is4xxClientError(), je nach Implementierung
    }

    @Test
    void shouldFailRegisterIfEmailExists() throws Exception {
        String email = "dupintegration-" + UUID.randomUUID() + "@test.com";
        String password = "pw123";
        createdEmails.add(email);

        // Register once
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().isOk());

        // Register again (should fail)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        String email = "failint-" + UUID.randomUUID() + "@test.com";
        String password = "pw123";
        createdEmails.add(email);

        // Register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().isOk());

        // Login with wrong password
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"wrongpw\"}")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }
}
