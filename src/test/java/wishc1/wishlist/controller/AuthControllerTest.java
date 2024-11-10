package wishc1.wishlist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import wishc1.wishlist.config.PasswordConfig;
import wishc1.wishlist.config.SecurityConfig;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;
import wishc1.wishlist.security.CustomUserDetails;
import wishc1.wishlist.security.CustomUserDetailsService;
import wishc1.wishlist.service.AppUserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, PasswordConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService;

    @MockBean
    private AppUserRepository appUserRepository;  // Add this mock to resolve dependency

    @MockBean
    private CustomUserDetailsService customUserDetailsService; // Mocked to satisfy SecurityConfig dependencies

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();  // Ensure a clean security context
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void showRegistrationForm_authenticatedUser_redirectToProfile() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        mockMvc.perform(get("/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }

    @Test
    void showRegistrationForm_unauthenticatedUser_returnsRegisterPage() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void registerUser_withErrors_returnsRegisterPage() throws Exception {
        mockMvc.perform(post("/register")
                        .flashAttr("appUser", new AppUser())) // Empty AppUser to trigger validation errors
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void registerUser_successfulRegistration_redirectsToLogin() throws Exception {
        AppUser appUser = new AppUser("test@example.com", "password123", "username");

        mockMvc.perform(post("/register")
                        .flashAttr("appUser", appUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("success", "Registration successful! Please log in."));

        verify(appUserService, times(1)).saveUser(any(AppUser.class));
    }

    @Test
    void showLoginForm_returnsLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void userProfile_authenticatedUser_returnsProfilePage() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        AppUser appUser = new AppUser("test@example.com", "password123", "username");
        when(customUserDetails.getAppUser()).thenReturn(appUser);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("appUser", appUser));
    }
}
