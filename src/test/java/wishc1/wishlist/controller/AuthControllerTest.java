package wishc1.wishlist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import wishc1.wishlist.exception.UserAlreadyExistsException;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;
import wishc1.wishlist.security.CustomUserDetails;
import wishc1.wishlist.security.CustomUserDetailsService;
import wishc1.wishlist.service.AppUserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    private AppUserRepository appUserRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Redirects authenticated user to profile when accessing registration page")
    void showRegistrationForm_authenticatedUser_redirectToProfile() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        mockMvc.perform(get("/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }

    @Test
    @DisplayName("Returns registration page for unauthenticated user")
    void showRegistrationForm_unauthenticatedUser_returnsRegisterPage() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("Returns register view on validation errors with 200 OK status")
    void registerReturnsViewOnValidationErrors() throws Exception {
        // Creating an invalid AppUser object to trigger validation errors
        AppUser invalidUser = new AppUser("", "", ""); // Blank fields for validation errors

        mockMvc.perform(post("/register")
                        .flashAttr("appUser", invalidUser) // Provide invalid AppUser
                        .with(csrf())) // Include CSRF to pass security check
                .andExpect(status().isOk()) // Expect 200 OK as it should reload the registration page
                .andExpect(view().name("register")) // Stay on registration view
                .andExpect(model().attributeHasFieldErrors("appUser", "email", "username", "password")); // Expect errors on all fields
    }


    @Test
    @DisplayName("Redirects to login on successful registration")
    void registerUser_successfulRegistration_redirectsToLogin() throws Exception {
        AppUser appUser = new AppUser("test@example.com", "password123", "username");

        mockMvc.perform(post("/register")
                        .flashAttr("appUser", appUser)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("success", "Registration successful! Please log in."));

        verify(appUserService, times(1)).saveUser(any(AppUser.class));
    }

    @Test
    @DisplayName("Displays email error on duplicate email during registration")
    void registerUser_withDuplicateEmail_showsEmailError() throws Exception {
        doThrow(new UserAlreadyExistsException("Email is already registered"))
                .when(appUserService).saveUser(any(AppUser.class));

        AppUser appUser = new AppUser("duplicate@example.com", "password123", "username");

        mockMvc.perform(post("/register")
                        .flashAttr("appUser", appUser)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("appUser", "email", "error.appUser"));
    }

    @Test
    @DisplayName("Displays username error on duplicate username during registration")
    void registerUser_withDuplicateUsername_showsUsernameError() throws Exception {
        doThrow(new UserAlreadyExistsException("Username is already taken"))
                .when(appUserService).saveUser(any(AppUser.class));

        AppUser appUser = new AppUser("user@example.com", "password123", "duplicateuser");

        mockMvc.perform(post("/register")
                        .flashAttr("appUser", appUser)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("appUser", "username", "error.appUser"));
    }

    @Test
    @DisplayName("Returns login page on GET /login")
    void showLoginForm_returnsLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("Returns profile page with user info for authenticated user")
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
