package oriongram.repos;

import org.springframework.data.repository.CrudRepository;
import oriongram.model.Image;

public interface ImageRepository extends CrudRepository<Image,Integer> {
}
