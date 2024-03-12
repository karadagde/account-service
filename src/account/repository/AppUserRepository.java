package account.repository;

import account.model.entity.AppUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    Optional<AppUser> findAppUserByEmail(String username);

    @Query("SELECT COUNT(u) FROM AppUser u")
    int countAppUsers();


    @Modifying
    @Query("UPDATE AppUser a SET a.failedAttempt = ?1 WHERE a.email = ?2")
    void updateFailedAttempts(int failedAttempt, String email);
}
