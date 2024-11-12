package wishc1.wishlist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import wishc1.wishlist.exception.UserAlreadyExistsException;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;

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

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);  // Call repository method
    }

    public AppUser saveUser(AppUser appUser) {
        // Check if email is already registered
        if (appUserRepository.existsByEmail(appUser.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        // Check if username is already taken
        if (appUserRepository.existsByUsername(appUser.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }
}


