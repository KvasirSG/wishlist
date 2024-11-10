package wishc1.wishlist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;
import wishc1.wishlist.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Find an AppUser by their email.
     *
     * @param email the email to search for
     * @return an Optional containing the AppUser if found, or empty if not found
     */
    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    /**
     * Save a new AppUser, encoding their password before saving.
     *
     * @param appUser the user to save
     * @return the saved AppUser
     */
    public AppUser saveUser(AppUser appUser) {
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
}
