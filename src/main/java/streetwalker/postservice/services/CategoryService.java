package streetwalker.postservice.services;

import org.springframework.stereotype.Service;
import streetwalker.postservice.models.Category;
import streetwalker.postservice.repositories.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public Category getCategory(String categoryName) {
       return categoryRepository.findByCategoryName(categoryName).orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
