package streetwalker.postservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private Long authorId;

    // --- Comments ---
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // --- Tags ---
    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    // --- Likes ---
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    // --- Category ---
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;
}



