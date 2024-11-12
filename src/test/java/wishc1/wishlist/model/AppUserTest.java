package wishc1.wishlist.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppUserTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validAppUserShouldPassValidation() {
        AppUser appUser = new AppUser("test@example.com", "password123", "username");
        Set<ConstraintViolation<AppUser>> violations = validator.validate(appUser);

        assertTrue(violations.isEmpty(), "Valid AppUser should have no validation errors");
    }

    @Test
    void invalidEmailShouldFailValidation() {
        AppUser appUser = new AppUser("invalidEmail", "validPassword123", "validUsername");
        Set<ConstraintViolation<AppUser>> violations = validator.validate(appUser);

        // Check that both the @Email and @Pattern messages are present
        long emailErrors = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("email") &&
                        (v.getMessage().equals("must be a well-formed email address") ||
                                v.getMessage().equals("Please enter a valid email address")))
                .count();

        // Expect two errors: one from @Email and one from @Pattern
        assertEquals(2, emailErrors, "AppUser with invalid email should have two validation errors for email");
    }

    @Test
    void shortPasswordShouldFailValidation() {
        AppUser appUser = new AppUser("test@example.com", "short", "username");
        Set<ConstraintViolation<AppUser>> violations = validator.validate(appUser);

        assertEquals(1, violations.size(), "AppUser with short password should have one validation error");
        assertEquals("Password must be at least 8 characters", violations.iterator().next().getMessage());
    }

    @Test
    void shortUsernameShouldFailValidation() {
        AppUser appUser = new AppUser("test@example.com", "password123", "us");
        Set<ConstraintViolation<AppUser>> violations = validator.validate(appUser);

        assertEquals(1, violations.size(), "AppUser with short username should have one validation error");
        assertEquals("Username must be at least 3 characters", violations.iterator().next().getMessage());
    }

    @Test
    void blankFieldsShouldFailValidation() {
        AppUser appUser = new AppUser("", "", "");
        Set<ConstraintViolation<AppUser>> violations = validator.validate(appUser);

        // Check if expected error messages are present
        boolean emailError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                (v.getMessage().equals("must not be blank") || v.getMessage().equals("Please enter a valid email address")));
        boolean passwordError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                v.getMessage().equals("Password must be at least 8 characters"));
        boolean usernameError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username") &&
                v.getMessage().equals("Username must be at least 3 characters"));

        assertTrue(emailError, "Email validation error for blank or invalid email should be present");
        assertTrue(passwordError, "Password validation error for minimum length should be present");
        assertTrue(usernameError, "Username validation error for minimum length should be present");
    }

}
