package streetwalker.postservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import streetwalker.postservice.dto.category.CategoryDTO;
import streetwalker.postservice.mappers.CategoryMapper;
import streetwalker.postservice.models.Category;
import streetwalker.postservice.repositories.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getCategory_ReturnsCategory_WhenFound() {
        Category category = new Category(1L, "Tech", "Tech category");
        when(categoryRepository.findByCategoryNameIgnoreCase("Tech")).thenReturn(Optional.of(category));

        Category result = categoryService.getCategory("Tech");

        assertNotNull(result);
        assertEquals("Tech", result.getCategoryName());
        verify(categoryRepository, times(1)).findByCategoryNameIgnoreCase("Tech");
    }

    @Test
    void getCategory_ThrowsException_WhenNotFound() {
        when(categoryRepository.findByCategoryNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.getCategory("Unknown"));

        assertEquals("Category not found", exception.getMessage());
    }
    @Test
    void createCategory_shouldSaveAndReturnCategory_whenNameNotExists() {
        // given
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName("Music");

        Category mappedCategory = new Category();
        mappedCategory.setCategoryName("Music");

        when(categoryRepository.existsByCategoryNameIgnoreCase("Music")).thenReturn(false);
        when(categoryMapper.toCategory(dto)).thenReturn(mappedCategory);
        when(categoryRepository.save(mappedCategory)).thenReturn(mappedCategory);

        // when
        Category result = categoryService.createCategory(dto);

        // then
        verify(categoryRepository).existsByCategoryNameIgnoreCase("Music");
        verify(categoryMapper).toCategory(dto);
        verify(categoryRepository).save(mappedCategory);

        assertEquals("Music", result.getCategoryName());
    }

    @Test
    void createCategory_shouldThrowException_whenNameAlreadyExists() {
        // given
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName("Books");

        when(categoryRepository.existsByCategoryNameIgnoreCase("Books")).thenReturn(true);

        // when + then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.createCategory(dto));

        assertEquals("Category name already exists", ex.getMessage());
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toCategory(any());
    }
}