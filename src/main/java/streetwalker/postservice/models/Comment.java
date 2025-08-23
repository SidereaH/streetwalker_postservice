package streetwalker.postservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
}
