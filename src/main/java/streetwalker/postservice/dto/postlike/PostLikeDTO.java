package streetwalker.postservice.dto.postlike;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostLikeDTO {
    private Long postId;
    private Long authorId;
}
