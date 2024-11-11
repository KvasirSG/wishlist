package wishc1.wishlist.config;

import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {

    private final SpringConstraintValidatorFactory springConstraintValidatorFactory;

    @Autowired
    public ValidationConfig(SpringConstraintValidatorFactory springConstraintValidatorFactory) {
        this.springConstraintValidatorFactory = springConstraintValidatorFactory;
    }

    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setConstraintValidatorFactory(springConstraintValidatorFactory);
        return factoryBean;
    }
}
