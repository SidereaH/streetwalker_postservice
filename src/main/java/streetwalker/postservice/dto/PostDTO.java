package streetwalker.postservice.dto;

import streetwalker.postservice.models.Category;
import streetwalker.postservice.models.Comment;
import streetwalker.postservice.models.Tag;

import java.time.OffsetDateTime;
import java.util.List;

public class PostDTO {
    private String title;
    private String content;
    private Long authorId;
    private Comment comment;
    private Integer likes;
    private List<Tag> tags;
    private Category category;
    private OffsetDateTime createdAt;
    private Boolean isUpdated;
}
