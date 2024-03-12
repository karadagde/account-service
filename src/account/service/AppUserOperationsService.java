package account.service;

import account.model.entity.AppUser;
import account.model.entity.Role;
import account.model.enums.UserRoles;
import account.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AppUserOperationsService {

    public static final int MAX_FAILED_ATTEMPTS = 5;


    private final AppUserRepository appUserRepository;

    public AppUserOperationsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }


    public void increaseFailedAttempts(AppUser user) {
        int failedAttempts = user.getFailedAttempt() + 1;
        appUserRepository.updateFailedAttempts(failedAttempts, user.getEmail());
    }

    public void lockUserAccount(AppUser user) {
        Role userRole = user.getRoles().stream()
                .filter(role -> role.getName()
                        .equals(UserRoles.ROLE_ADMINISTRATOR.name()))
                .findAny()
                .orElse(null);
        if (userRole != null) {
            return;
        }
        user.setAccountNonLocked(false);
        appUserRepository.save(user);
    }


    public void resetFailedAttempts(AppUser user) {
        appUserRepository.updateFailedAttempts(0, user.getEmail());
    }

    public void unlockUserAccount(AppUser user) {
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        appUserRepository.save(user);
    }


}
