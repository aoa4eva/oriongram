package oriongram.repos;

import org.springframework.data.repository.CrudRepository;
import oriongram.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String u);
    User findByEmail(String e);
    boolean existsByEmail(String e);
    boolean existsByUsername(String u);
    Integer countByEmail(String e);
}
