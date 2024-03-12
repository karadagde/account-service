package account.model.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class SimplifiedUserDTO {

    public Long id;
    public String name;
    public String lastname;
    public String email;
    public List<String> roles;


    public List<String> getRoles() {
        Collections.sort(roles);
        return roles;
    }
}
