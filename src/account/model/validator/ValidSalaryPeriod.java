package account.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = SalaryPeriodValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidSalaryPeriod {

    String message() default "Invalid period format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
