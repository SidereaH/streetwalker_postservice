package streetwalker.postservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    @OneToOne
    private Comment comment;

    @OneToMany
    private List<Tag> tags;
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PostLike> likes;
    @ManyToOne
    private Category category;
    @CreatedDate
    private OffsetDateTime createdAt;
    @LastModifiedDate
    private OffsetDateTime updatedAt;

}
