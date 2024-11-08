package wishc1.wishlist.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import wishc1.wishlist.model.validation.UniqueUsername;
import wishc1.wishlist.repository.AppUserRepository;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && !appUserRepository.existsByUsername(username);
    }
}

