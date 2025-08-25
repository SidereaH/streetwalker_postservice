package streetwalker.postservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import streetwalker.postservice.dto.PostCreateDTO;
import streetwalker.postservice.dto.PostDTO;
import streetwalker.postservice.dto.PostUpdateDTO;
import streetwalker.postservice.dto.TagDTO;
import streetwalker.postservice.models.Post;
import streetwalker.postservice.models.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "countLikes")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTagsToDTO")
    @Mapping(target = "isUpdated", expression = "java(isPostUpdated(post))")
    @Mapping(target = "comments", ignore = true)
    PostDTO toDTO(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true) // Обработаем отдельно в сервисе
    Post fromCreateDTO(PostCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true) // Обработаем отдельно в сервисе
    void updateFromDTO(PostUpdateDTO dto, @org.mapstruct.MappingTarget Post post);

    @Named("countLikes")
    default Integer countLikes(List<?> likes) {
        return likes != null ? likes.size() : 0;
    }

    @Named("mapTagsToDTO")
    default List<TagDTO> mapTagsToDTO(List<Tag> tags) {
        if (tags == null) {
            return null;
        }
        return tags.stream()
                .map(tag -> new TagDTO(tag.getTagName(), tag.getTagDescription()))
                .collect(Collectors.toList());
    }

    default Boolean isPostUpdated(Post post) {
        return post.getCreatedAt() != null &&
                post.getUpdatedAt() != null &&
                !post.getCreatedAt().isEqual(post.getUpdatedAt());
    }
}