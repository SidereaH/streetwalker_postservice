package streetwalker.postservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import streetwalker.postservice.models.PostLike;
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Boolean existsByAuthorIdAndLikeId(Long authorId, Long likeId);
}
