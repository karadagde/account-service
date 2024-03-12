package account.model.dto.requests;

import account.model.validator.ValidEmailDomain;
import account.model.validator.ValidSalaryPeriod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryUploadRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @ValidEmailDomain(domain = "acme.com",
                      message = "Email must be from acme.com")
    private String employee;


    @NotBlank(message = "Period is required")
    @ValidSalaryPeriod
    private String period;

    @NotNull(message = "Salary is mandatory")
    @Min(value = 1, message = "Salary must be greater than 0")
    private Long salary;

}
