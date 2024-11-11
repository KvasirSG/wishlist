package wishc1.wishlist.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {

    private final ApplicationContext applicationContext;

    @Autowired
    public SpringConstraintValidatorFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return applicationContext.getBean(key);  // Get the bean from Spring context
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        // No-op, Spring will handle the lifecycle of beans
    }
}

