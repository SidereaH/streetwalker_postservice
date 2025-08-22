package streetwalker.postservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import streetwalker.postservice.models.PostLikeId;
import streetwalker.postservice.models.Tag;
@Repository
public interface TagRepository extends JpaRepository<Tag, PostLikeId>{
}
