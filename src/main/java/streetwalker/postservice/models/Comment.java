package streetwalker.postservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Long authorId;

    // --- Reference to Post ---
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // --- Self-reference (nested comments) ---
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(content, comment.content) && Objects.equals(authorId, comment.authorId) && Objects.equals(post, comment.post) && Objects.equals(parentComment, comment.parentComment) && Objects.equals(replies, comment.replies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, authorId, post, parentComment, replies);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", post=" + post +
                ", parentComment=" + parentComment +
                ", replies=" + replies +
                '}';
    }
}
