package account.model.dto.requests;

import account.model.enums.OperationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateUserRolesRequestDTO {

    @Email
//    @ValidEmailDomain(domain = "acme.com",
//                      message = "Email must be from acme.com")
    private String user;

    @NotNull
    private String role;

    @NotNull
    private OperationType operation;

    public String getUser() {
        return user.toLowerCase();
    }

    public void setUser(String user) {
        this.user = user.toLowerCase();
    }


}
