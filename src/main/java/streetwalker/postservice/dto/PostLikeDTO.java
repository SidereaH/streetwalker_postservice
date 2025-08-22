package streetwalker.postservice.dto;

import jakarta.persistence.GeneratedValue;
import lombok.Getter;

@Getter
public class PostLikeDTO {
    private Long postId;
    private Long authorId;
}
