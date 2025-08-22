package streetwalker.postservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import streetwalker.postservice.models.Comment;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
