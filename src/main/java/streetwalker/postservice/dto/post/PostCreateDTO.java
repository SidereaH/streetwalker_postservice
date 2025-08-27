package streetwalker.postservice.dto.post;

import lombok.Getter;
import lombok.Setter;
import streetwalker.postservice.dto.category.CategoryDTO;

import java.util.List;
@Getter
@Setter
public class PostCreateDTO {
    private Long authorId;
    private String title;
    private String content;
    private List<String> tags;
    private CategoryDTO category;
}
