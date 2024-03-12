package account.model.dto.data;

import account.model.entity.AppUser;
import account.model.entity.Group;
import account.model.entity.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


@Getter
@Setter
@ToString
public class AppUserDTO implements UserDetails {

    private final AppUser user;

    public AppUserDTO(AppUser user) {
        this.user = user;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Set<Role> authorities = this.user.getRoles();
        Collection<GrantedAuthority> grantedAuthorities =
                new ArrayList<>();
        authorities.forEach(role -> grantedAuthorities.add(
                (GrantedAuthority) role::getName));
        return grantedAuthorities;

    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Group getUserGroup() {
        return this.user.getGroup();
    }

    public Long getId() {
        return this.user.getId();
    }

    public int getFailedAttempt() {
        return this.user.getFailedAttempt();
    }

}
