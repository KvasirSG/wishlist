package wishc1.wishlist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import wishc1.wishlist.exception.UserAlreadyExistsException;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

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
        return appUserRepository.findByEmail(email);  // Call repository method
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
}


