package streetwalker.postservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import streetwalker.postservice.models.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
