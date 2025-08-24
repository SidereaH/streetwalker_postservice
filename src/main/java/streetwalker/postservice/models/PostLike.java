package streetwalker.postservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostLike extends Like {

    @ManyToOne
    @MapsId("objectId")
    private Post post;

    public PostLike(Post post, Long authorId) {
        this.post = post;
        if (post.getId() == null) {
            throw new IllegalArgumentException("Post must have an ID");
        }
        super.setId(new LikeId(post.getId(), authorId));
    }
    public LikeId getId(){
        return super.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostLike postLike = (PostLike) o;
        return Objects.equals(super.getId(), postLike.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId());
    }
}

