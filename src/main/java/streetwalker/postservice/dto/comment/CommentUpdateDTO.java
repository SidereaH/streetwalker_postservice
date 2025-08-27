package streetwalker.postservice.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentUpdateDTO {
    private Long commentId;
    private String newContent;
}
