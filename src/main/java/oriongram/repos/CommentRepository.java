package oriongram.repos;

import org.springframework.data.repository.CrudRepository;
import oriongram.model.Comment;

import java.util.ArrayList;

/**
 * Created by student on 7/12/17.
 */
public interface CommentRepository extends CrudRepository<Comment, Integer> {
    ArrayList<Comment> findByImageId(int id);
}
