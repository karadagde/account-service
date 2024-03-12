package account.repository;

import account.model.entity.InfoSecEvent;
import org.springframework.data.repository.CrudRepository;

public interface InfoSecEventRepository
        extends CrudRepository<InfoSecEvent, Long> {


}
