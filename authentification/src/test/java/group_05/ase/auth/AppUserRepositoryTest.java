package group_05.ase.auth;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;


@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void shouldSaveAndFindAppUserByEmail() {
        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        user.setSupabaseId(UUID.randomUUID());
        user.setDisplayName("Max Mustermann");
        AppUser saved = appUserRepository.save(user);

        Optional<AppUser> found = appUserRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }
}

