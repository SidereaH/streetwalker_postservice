package streetwalker.postservice.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter

public abstract class Like {
     @EmbeddedId
     private LikeId id;

}
