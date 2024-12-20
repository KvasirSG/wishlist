package wishc1.wishlist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import wishc1.wishlist.exception.UserAlreadyExistsException;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;
import wishc1.wishlist.security.CustomUserDetails;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public AppUser saveUser(AppUser appUser) {
        // Check if email is already registered
        if (appUserRepository.existsByEmail(appUser.getEmail())) {
            String message = messageSource.getMessage("error.email.in.use", null, LocaleContextHolder.getLocale());
            throw new UserAlreadyExistsException(message);
        }

        // Check if username is already taken
        if (appUserRepository.existsByUsername(appUser.getUsername())) {
            String message = messageSource.getMessage("error.username.in.use", null, LocaleContextHolder.getLocale());
            throw new UserAlreadyExistsException(message);
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    /**
     * Retrieve all AppUsers in the system.
     *
     * @return a List of all AppUsers
     */
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }
    /**
     * Retrieve the currently logged-in AppUser from the security context.
     *
     * @return the currently logged-in AppUser
     * @throws RuntimeException if the user is not authenticated
     */
    public AppUser getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getAppUser();
        } else if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found in the database."));
        } else if (principal instanceof String) {
            return appUserRepository.findByEmail((String) principal)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found in the database."));
        }

        throw new RuntimeException("No authenticated user found.");
    }
    public List<AppUser> getUsersByEmails(List<String> emails) {
        return appUserRepository.findAllByEmailIn(emails);
    }
    public List<AppUser> getUsersByUsernames(List<String> usernames) {
        return appUserRepository.findAllByUsernameIn(usernames);
    }
}
