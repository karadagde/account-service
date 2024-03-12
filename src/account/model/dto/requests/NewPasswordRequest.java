package account.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class NewPasswordRequest {
    @NotBlank
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String new_password;


}
