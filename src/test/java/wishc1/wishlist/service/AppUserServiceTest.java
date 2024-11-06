package wishc1.wishlist.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;

import java.util.Optional;

class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser_WithEncodedPassword() {
        AppUser user = new AppUser("test@example.com", "plainpassword", "testuser");
        String encodedPassword = "encodedpassword";

        when(passwordEncoder.encode("plainpassword")).thenReturn(encodedPassword);
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

        AppUser savedUser = appUserService.saveUser(user);

        assertEquals(encodedPassword, savedUser.getPassword());
        verify(appUserRepository, times(1)).save(user);
    }

    @Test
    void testFindByEmail_UserExists() {
        AppUser user = new AppUser("test@example.com", "encodedpassword", "testuser");
        when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<AppUser> foundUser = appUserService.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }
}
