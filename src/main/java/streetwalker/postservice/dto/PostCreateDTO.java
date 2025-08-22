package streetwalker.postservice.dto;

import streetwalker.postservice.models.Category;
import streetwalker.postservice.models.Tag;

import java.util.List;

public class PostCreateDTO {
    private Long authorId;
    private String title;
    private String content;
    private List<Tag> tags;
    private Category category;
}
