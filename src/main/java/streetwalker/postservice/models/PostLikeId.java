package streetwalker.postservice.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeId implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;
    private Long authorId;
    public PostLikeId( Long authorId) {
        this.authorId = likeId;
    }
}
