package streetwalker.postservice.dto;

import lombok.Getter;
import streetwalker.postservice.models.Category;
import streetwalker.postservice.models.Tag;

import java.util.List;
@Getter
public class PostCreateDTO {
    private Long authorId;
    private String title;
    private String content;
    private List<Tag> tags;
    private Category category;
}
