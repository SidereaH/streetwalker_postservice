package streetwalker.postservice.dto.post;

import lombok.Getter;
import lombok.Setter;
import streetwalker.postservice.dto.category.CategoryDTO;
import streetwalker.postservice.dto.tag.TagDTO;
import streetwalker.postservice.dto.comment.CommentDTO;

import java.time.OffsetDateTime;
import java.util.List;
@Getter
@Setter
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private List<CommentDTO> comments;
    private Integer likes;
    private List<TagDTO> tags;
    private CategoryDTO category;
    private OffsetDateTime createdAt;
    private Boolean isUpdated;
}