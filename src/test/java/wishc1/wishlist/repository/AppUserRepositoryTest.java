package wishc1.wishlist.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import wishc1.wishlist.model.AppUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        // Initialize a test user and save to repository before each test
        testUser = new AppUser("test@example.com", "testPassword123", "testUser");
        appUserRepository.save(testUser);
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        // Act
        Optional<AppUser> foundUser = appUserRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(foundUser.isPresent(), "User should be found by email");
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        // Act
        Optional<AppUser> foundUser = appUserRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundUser.isPresent(), "No user should be found with a non-existent email");
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        // Act
        boolean exists = appUserRepository.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists, "existsByEmail should return true when email exists");
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        // Act
        boolean exists = appUserRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists, "existsByEmail should return false when email does not exist");
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        // Act
        boolean exists = appUserRepository.existsByUsername("testUser");

        // Assert
        assertTrue(exists, "existsByUsername should return true when username exists");
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        // Act
        boolean exists = appUserRepository.existsByUsername("nonexistentUser");

        // Assert
        assertFalse(exists, "existsByUsername should return false when username does not exist");
    }
}

