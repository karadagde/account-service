package account.repository;

import account.model.entity.Group;
import account.model.enums.UserGroupCodes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
    Group findByCode(UserGroupCodes code);

    @Query("SELECT g FROM Group g JOIN FETCH g.roles r WHERE r.id = ?1")
    Group findByRoleId(Long roleId);
}
