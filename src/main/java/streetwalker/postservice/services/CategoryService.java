package streetwalker.postservice.services;

import org.springframework.stereotype.Service;
import streetwalker.postservice.dto.category.CategoryDTO;
import streetwalker.postservice.mappers.CategoryMapper;
import streetwalker.postservice.models.Category;
import streetwalker.postservice.repositories.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }
    public Category getCategory(String categoryName) {
       return categoryRepository.findByCategoryNameIgnoreCase(categoryName).orElseThrow(() -> new RuntimeException("Category not found"));
    }
    public Category createCategory(CategoryDTO category) {
        if (categoryRepository.existsByCategoryNameIgnoreCase(category.getCategoryName())  ) {
            throw new RuntimeException("Category name already exists");
        }
        return categoryRepository.save( categoryMapper.toCategory(category));
    }
}
