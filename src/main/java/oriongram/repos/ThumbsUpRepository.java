package oriongram.repos;

import org.springframework.data.repository.CrudRepository;
import oriongram.model.ThumbsUp;

import java.util.ArrayList;

public interface ThumbsUpRepository extends CrudRepository<ThumbsUp, Integer> {
    ArrayList<ThumbsUp> findAllByUsername(String username);
    ArrayList<ThumbsUp> findAllByImageId(int id);
}
