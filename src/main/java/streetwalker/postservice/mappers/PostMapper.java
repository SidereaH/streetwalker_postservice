package streetwalker.postservice.mappers;

import org.mapstruct.*;
import streetwalker.postservice.dto.PostCreateDTO;
import streetwalker.postservice.dto.PostDTO;
import streetwalker.postservice.models.Post;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {  }
)
public interface PostMapper {
    @Mapping(target = "isUpdated", source = "post")
    @Mapping(target = "likes", source = "post")
    PostDTO map(Post post);
    default Integer mapLikes(Post post) {
        return post.getCommentLike().size();
    }
    default Boolean mapIsUpdated(Post post) {
        return post.getUpdatedAt() != null && !post.getUpdatedAt().equals(post.getCreatedAt());
    }
    Post map(PostCreateDTO postDTO);
    void updatePost(@MappingTarget Post post, PostDTO postDTO);
}
