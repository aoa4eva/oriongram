package oriongram.repos;


import org.springframework.data.repository.CrudRepository;
import oriongram.model.Follow;

import java.util.ArrayList;

public interface FollowRepository extends CrudRepository<Follow,Integer> {

    ArrayList<Follow> findAllByFollower(String username);

}
