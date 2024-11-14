package wishc1.wishlist.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.security.CustomUserDetailsService;

import java.util.Optional;

class CustomUserDetailsServiceTest {

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        AppUser user = new AppUser("test@example.com", "encodedpassword", "testuser");
        when(appUserService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedpassword", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(appUserService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("nonexistent@example.com")
        );
    }
}

