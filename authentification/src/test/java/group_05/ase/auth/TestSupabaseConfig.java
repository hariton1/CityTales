package group_05.ase.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest  // This tells Spring Boot to load the full context for testing
@ActiveProfiles("test")  // Use the test profile for configuration
public class TestSupabaseConfig {

    // Inject configuration properties from application-test.properties
    @Value("${supabase.project-url}")
    private String supabaseProjectUrl;

    @Value("${supabase.service-role-key}")
    private String supabaseServiceRoleKey;

    // Spring will inject the ApplicationContext for us
    private final ApplicationContext applicationContext;

    // Constructor injection for ApplicationContext
    public TestSupabaseConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // Bean for mocking SupabaseService
    @Bean
    public SupabaseService supabaseService() {
        return Mockito.mock(SupabaseService.class);
    }

    @BeforeEach
    void setUp() {
        // You can setup your mock or test environment here if needed
    }

    @Test
    void testSupabaseConfigProperties() {
        // Test that the properties are loaded and injected correctly
        assertNotNull(supabaseProjectUrl, "Supabase project URL should not be null");
        assertNotNull(supabaseServiceRoleKey, "Supabase service role key should not be null");

        // Optionally, you can also validate that the properties have expected values
        assertEquals("https://your-supabase-url", supabaseProjectUrl);
        assertEquals("your-service-role-key", supabaseServiceRoleKey);
    }

    @Test
    void testSupabaseServiceBeanExists() {
        // Test that the SupabaseService bean is loaded in the Spring context
        SupabaseService supabaseService = applicationContext.getBean(SupabaseService.class);
        assertNotNull(supabaseService, "SupabaseService bean should be created and injected");
    }
}
