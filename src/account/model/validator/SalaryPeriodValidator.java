package account.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryPeriodValidator
        implements ConstraintValidator<ValidSalaryPeriod, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        // date format MM-yyyy
        String regex = "(0[1-9]|1[0-2])-[0-9]{4}";

        return value.matches(regex);

    }
}
