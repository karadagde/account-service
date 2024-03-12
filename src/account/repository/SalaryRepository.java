package account.repository;

import account.model.entity.Salary;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends CrudRepository<Salary, Long> {
    List<Salary> findAllByEmail(String email);

    Optional<Salary> findSalaryByEmailAndPeriod(String email, LocalDate period);
}
