package account.repository;

import account.model.entity.BreachedPassword;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BreachedPasswordRepository
        extends CrudRepository<BreachedPassword, Long> {
    Optional<BreachedPassword> findByPassword(String password);
}
