package streetwalker.postservice.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Like {
    @EmbeddedId
     private LikeId id;

}
