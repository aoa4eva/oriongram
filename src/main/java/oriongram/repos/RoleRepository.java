package oriongram.repos;

import org.springframework.data.repository.CrudRepository;
import oriongram.model.Role;

public interface RoleRepository extends CrudRepository<Role, Integer>{
    Role findByRole(String r);
    boolean existsByRole(String r);
}