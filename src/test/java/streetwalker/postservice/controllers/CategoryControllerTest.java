package streetwalker.postservice.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import streetwalker.postservice.controllers.CategoryController;
import streetwalker.postservice.dto.category.CategoryDTO;
import streetwalker.postservice.models.Category;
import streetwalker.postservice.services.CategoryService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getCategory_Success() throws Exception {
        Category category = new Category(1L, "Tech", "desc");
        when(categoryService.getCategory("Tech")).thenReturn(category);

        mockMvc.perform(get("/api/categories/Tech").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Tech"));
    }

    @Test
    void getCategory_NotFound() throws Exception {
        when(categoryService.getCategory("Unknown")).thenThrow(new RuntimeException("Category not found"));

        mockMvc.perform(get("/api/categories/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }

    @Test
    void createCategory_Success() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName("Tech");
        dto.setCategoryDescription("desc");
        Category saved = new Category(1L, "Tech", "desc");

        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"Tech\",\"description\":\"desc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("Tech"));
    }

    @Test
    void createCategory_AlreadyExists() throws Exception {
        when(categoryService.createCategory(any())).thenThrow(new RuntimeException("Category name already exists"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"Tech\",\"description\":\"desc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Category name already exists"));
    }
}
