package streetwalker.postservice.dto;

import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostLikeDTO {
    private Long postId;
    private Long authorId;
}
