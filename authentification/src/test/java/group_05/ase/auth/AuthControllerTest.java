package group_05.ase.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSupabaseConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserRepository userRepository;

    @Mock
    private SupabaseService supabaseService;

    private final List<String> createdEmails = new ArrayList<>();

    @Test
    void shouldRegisterAndLoginViaApi() throws Exception {
        // Mock the Supabase user creation to return a valid user ID
        Mockito.when(supabaseService.createUser(Mockito.anyString(), Mockito.anyString()))
                .thenReturn("valid-uuid");  // Fake valid UUID for the test

        // Now test the registration API
        mockMvc.perform(post("/auth/register")
                        .with(csrf())  // Add CSRF token to the request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk());  // Check that the response is OK

        // Now test the login API
        mockMvc.perform(post("/auth/login")
                        .with(csrf())  // Add CSRF token to the request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk());  // Expect OK status for successful login
    }


//    @Test
//    void shouldRegisterAndLoginViaApi() throws Exception {
//        // Mock the Supabase user creation to return a valid user ID
//        Mockito.when(supabaseService.createUser(Mockito.anyString(), Mockito.anyString()))
//                .thenReturn("valid-uuid");  // Fake valid UUID for the test
//
//        // Now test the registration API
//        mockMvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
//                .andExpect(status().isOk());  // Expect OK status for successful registration
//
//        // Now test the login API
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
//                .andExpect(status().isOk());  // Expect OK status for successful login
//    }



    @AfterEach
    void cleanupAfter() {
        for (String email : createdEmails) {
            userRepository.findByEmail(email).ifPresent(userRepository::delete);
        }
        createdEmails.clear();
    }

    @Test
    void shouldFailOnDuplicateRegistration() throws Exception {
        String email = "dup-" + UUID.randomUUID() + "@test.com";
        String password = "test123";
        createdEmails.add(email);

        // First registration OK
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().isOk());

        // Second registration should fail
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        String email = "loginfail-" + UUID.randomUUID() + "@test.com";
        String password = "test123";
        createdEmails.add(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"wrongpass\"}")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

}
