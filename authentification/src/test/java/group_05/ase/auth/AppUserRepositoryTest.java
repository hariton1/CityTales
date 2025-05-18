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
        AppUser saved = appUserRepository.save(user);

        Optional<AppUser> found = appUserRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void shouldReturnEmptyIfUserNotExists() {
        Optional<AppUser> found = appUserRepository.findByEmail("doesnotexist@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldNotMixUpUsers() {
        AppUser user1 = new AppUser();
        user1.setEmail("one@example.com");
        user1.setSupabaseId(UUID.randomUUID());
        appUserRepository.save(user1);

        AppUser user2 = new AppUser();
        user2.setEmail("two@example.com");
        user2.setSupabaseId(UUID.randomUUID());
        appUserRepository.save(user2);

        Optional<AppUser> found = appUserRepository.findByEmail("two@example.com");
        assertTrue(found.isPresent());
        assertEquals("two@example.com", found.get().getEmail());
    }



}

