package wishc1.wishlist.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wishc1.wishlist.model.validation.UniqueEmail;
import wishc1.wishlist.repository.AppUserRepository;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final AppUserRepository appUserRepository;

    public UniqueEmailValidator(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && !appUserRepository.existsByEmail(email);
    }
}

