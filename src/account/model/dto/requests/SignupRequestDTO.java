package account.model.dto.requests;

import account.model.validator.ValidEmailDomain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class SignupRequestDTO {


    @NotBlank(message = "Name is required")
    private String name;


    @NotBlank(message = "Last name is required")
    private String lastname;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @ValidEmailDomain(domain = "acme.com",
                      message = "Email must be from acme.com")
    private String email;


    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String password;


    public SignupRequestDTO(String name, String lastname, String email,
                            String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email.toLowerCase();
        this.password = password;
    }

    public SignupRequestDTO() {
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

}
