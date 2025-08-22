package streetwalker.postservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import streetwalker.postservice.models.Post;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}
