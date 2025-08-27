package streetwalker.postservice.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    private Long postId;
    private Long parentCommentId;
    private Long authorId;
    private String content;
}

