package streetwalker.postservice.dto;

import lombok.Getter;
import lombok.Setter;

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
