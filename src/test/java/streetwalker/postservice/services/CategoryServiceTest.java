package streetwalker.postservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import streetwalker.postservice.models.Category;
import streetwalker.postservice.repositories.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository ;
    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getCategory_ReturnsCategory_WhenFound() {
        Category category = new Category(1L, "Tech", "Tech category");
        when(categoryRepository.findByCategoryName("Tech")).thenReturn(Optional.of(category));

        Category result = categoryService.getCategory("Tech");

        assertNotNull(result);
        assertEquals("Tech", result.getCategoryName());
        verify(categoryRepository, times(1)).findByCategoryName("Tech");
    }

    @Test
    void getCategory_ThrowsException_WhenNotFound() {
        when(categoryRepository.findByCategoryName("Unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.getCategory("Unknown"));

        assertEquals("Category not found", exception.getMessage());
    }
}