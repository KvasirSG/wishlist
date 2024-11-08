package wishc1.wishlist.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import wishc1.wishlist.validator.UniqueUsernameValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "Username is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

