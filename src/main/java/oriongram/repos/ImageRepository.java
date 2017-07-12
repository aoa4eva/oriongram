package oriongram.repos;

import org.springframework.data.repository.CrudRepository;
import oriongram.model.Image;

import java.util.ArrayList;

public interface ImageRepository extends CrudRepository<Image,Integer> {
    ArrayList<Image> findAllByUsername(String username);
}
