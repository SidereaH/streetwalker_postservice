package streetwalker.postservice.dto.comment;

import lombok.Getter;

@Getter
public class CommentDTO {
    private Long postId;
    private Long parentCommentId;
    private Long authorId;
    private String content;
}
