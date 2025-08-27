package streetwalker.postservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import streetwalker.postservice.models.LikeId;
import streetwalker.postservice.models.Tag;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, LikeId>{
    Optional<Tag> findTagByTagName(String tag);
}
