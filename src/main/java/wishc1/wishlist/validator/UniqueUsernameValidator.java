package wishc1.wishlist.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wishc1.wishlist.model.validation.UniqueUsername;
import wishc1.wishlist.repository.AppUserRepository;

@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private final AppUserRepository appUserRepository;

    public UniqueUsernameValidator(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && !appUserRepository.existsByUsername(username);
    }
}

