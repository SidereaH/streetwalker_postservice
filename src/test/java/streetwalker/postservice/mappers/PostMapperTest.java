package streetwalker.postservice.mappers;


import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import streetwalker.postservice.dto.post.PostCreateDTO;
import streetwalker.postservice.dto.post.PostDTO;
import streetwalker.postservice.dto.post.PostUpdateDTO;
import streetwalker.postservice.dto.tag.TagDTO;
import streetwalker.postservice.models.Post;
import streetwalker.postservice.models.PostLike;
import streetwalker.postservice.models.Tag;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Test
    void toDTO_WithAllFields_ShouldMapCorrectly() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setAuthorId(100L);
        post.setCreatedAt(OffsetDateTime.now().minusHours(2));
        post.setUpdatedAt(OffsetDateTime.now().minusHours(1));

        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        tag1.setTagName("tech");
        tag1.setTagDescription("Technology related");
        tags.add(tag1);

        Tag tag2 = new Tag();
        tag2.setTagName("java");
        tag2.setTagDescription("Java programming");
        tags.add(tag2);
        post.setTags(tags);

        List<PostLike> likes = new ArrayList<>();
        likes.add(new PostLike());
        likes.add(new PostLike());
        post.setLikes(likes);

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
//        assertEquals(1L, result.ge());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals(100L, result.getAuthorId());
        assertEquals(2, result.getLikes());
        assertTrue(result.getIsUpdated());

        assertNotNull(result.getTags());
        assertEquals(2, result.getTags().size());
        assertEquals("tech", result.getTags().get(0).getName());
        assertEquals("Technology related", result.getTags().get(0).getDescription());
        assertEquals("java", result.getTags().get(1).getName());
        assertEquals("Java programming", result.getTags().get(1).getDescription());
    }

    @Test
    void toDTO_WithNullLikes_ShouldReturnZeroLikes() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setLikes(null);

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getLikes());
    }

    @Test
    void toDTO_WithEmptyLikes_ShouldReturnZeroLikes() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setLikes(new ArrayList<>());

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getLikes());
    }

    @Test
    void toDTO_WithNullTags_ShouldReturnNullTags() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setTags(null);

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertNull(result.getTags());
    }

    @Test
    void toDTO_WithEmptyTags_ShouldReturnEmptyTagsList() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setTags(new ArrayList<>());

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTags());
        assertTrue(result.getTags().isEmpty());
    }

    @Test
    void toDTO_WithSameCreatedAndUpdatedDate_ShouldReturnIsUpdatedFalse() {
        // Arrange
        OffsetDateTime now = OffsetDateTime.now();
        Post post = new Post();
        post.setId(1L);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsUpdated());
    }

    @Test
    void toDTO_WithNullDates_ShouldReturnIsUpdatedFalse() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setCreatedAt(null);
        post.setUpdatedAt(null);

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsUpdated());
    }

    @Test
    void toDTO_WithOnlyCreatedDate_ShouldReturnIsUpdatedFalse() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setCreatedAt(OffsetDateTime.now());
        post.setUpdatedAt(null);

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsUpdated());
    }

    @Test
    void toDTO_WithOnlyUpdatedDate_ShouldReturnIsUpdatedFalse() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setCreatedAt(null);
        post.setUpdatedAt(OffsetDateTime.now());

        // Act
        PostDTO result = postMapper.toDTO(post);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsUpdated());
    }

    @Test
    void fromCreateDTO_ShouldMapBasicFields() {
        // Arrange
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("New Post");
        dto.setContent("New Content");
        dto.setAuthorId(200L);

        // Act
        Post result = postMapper.fromCreateDTO(dto);

        // Assert
        assertNotNull(result);
        assertNull(result.getId()); // Должно игнорироваться
        assertEquals("New Post", result.getTitle());
        assertEquals("New Content", result.getContent());
        assertEquals(200L, result.getAuthorId());
        assertNull(result.getUpdatedAt());
        assertNull(result.getCategory());
    }

    @Test
    void fromCreateDTO_WithNullDTO_ShouldReturnNull() {
        // Act
        Post result = postMapper.fromCreateDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void updateFromDTO_ShouldUpdateOnlyAllowedFields() {
        // Arrange
        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Old Title");
        existingPost.setContent("Old Content");
        existingPost.setAuthorId(100L);
        existingPost.setCreatedAt(OffsetDateTime.now().minusDays(1));
        existingPost.setUpdatedAt(OffsetDateTime.now().minusHours(1));

        PostUpdateDTO updateDTO = new PostUpdateDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setContent("Updated Content");

        // Act
        postMapper.updateFromDTO(updateDTO, existingPost);

        // Assert
        assertEquals(1L, existingPost.getId()); // Не должно измениться
        assertEquals("Updated Title", existingPost.getTitle());
        assertEquals("Updated Content", existingPost.getContent());
        assertEquals(100L, existingPost.getAuthorId()); // Не должно измениться
        assertNotNull(existingPost.getCreatedAt()); // Не должно измениться
        assertNotNull(existingPost.getUpdatedAt()); // Не должно измениться
    }

    @Test
    void updateFromDTO_WithNullDTO_ShouldNotChangePost() {
        // Arrange
        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Original Title");
        existingPost.setContent("Original Content");

        // Act
        postMapper.updateFromDTO(null, existingPost);

        // Assert
        assertEquals(1L, existingPost.getId());
        assertEquals("Original Title", existingPost.getTitle());
        assertEquals("Original Content", existingPost.getContent());
    }

    @Test
    void updateFromDTO_WithNullFieldsInDTO_ShouldSetNullValues() {
        // Arrange
        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Original Title");
        existingPost.setContent("Original Content");

        PostUpdateDTO updateDTO = new PostUpdateDTO();
        updateDTO.setTitle(null);
        updateDTO.setContent(null);

        // Act
        postMapper.updateFromDTO(updateDTO, existingPost);

        // Assert
        assertEquals(1L, existingPost.getId());
        assertNull(existingPost.getTitle());
        assertNull(existingPost.getContent());
    }

    @Test
    void countLikes_WithNullList_ShouldReturnZero() {
        // Act
        Integer result = postMapper.countLikes(null);

        // Assert
        assertEquals(0, result);
    }

    @Test
    void countLikes_WithEmptyList_ShouldReturnZero() {
        // Act
        Integer result = postMapper.countLikes(new ArrayList<>());

        // Assert
        assertEquals(0, result);
    }

    @Test
    void countLikes_WithItems_ShouldReturnCount() {
        // Arrange
        List<Object> likes = List.of(new Object(), new Object(), new Object());

        // Act
        Integer result = postMapper.countLikes(likes);

        // Assert
        assertEquals(3, result);
    }

    @Test
    void mapTagsToDTO_WithNullList_ShouldReturnNull() {
        // Act
        List<TagDTO> result = postMapper.mapTagsToDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void mapTagsToDTO_WithEmptyList_ShouldReturnEmptyList() {
        // Act
        List<TagDTO> result = postMapper.mapTagsToDTO(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapTagsToDTO_WithTags_ShouldMapCorrectly() {
        // Arrange
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        tag1.setTagName("tag1");
        tag1.setTagDescription("description1");
        tags.add(tag1);

        Tag tag2 = new Tag();
        tag2.setTagName("tag2");
        tag2.setTagDescription("description2");
        tags.add(tag2);

        // Act
        List<TagDTO> result = postMapper.mapTagsToDTO(tags);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("tag1", result.get(0).getName());
        assertEquals("description1", result.get(0).getDescription());

        assertEquals("tag2", result.get(1).getName());
        assertEquals("description2", result.get(1).getDescription());
    }

    @Test
    void isPostUpdated_WithDifferentDates_ShouldReturnTrue() {
        // Arrange
        Post post = new Post();
        post.setCreatedAt(OffsetDateTime.now().minusHours(2));
        post.setUpdatedAt(OffsetDateTime.now().minusHours(1));

        // Act
        Boolean result = postMapper.isPostUpdated(post);

        // Assert
        assertTrue(result);
    }

    @Test
    void isPostUpdated_WithSameDates_ShouldReturnFalse() {
        // Arrange
        OffsetDateTime now = OffsetDateTime.now();
        Post post = new Post();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        // Act
        Boolean result = postMapper.isPostUpdated(post);

        // Assert
        assertFalse(result);
    }

    @Test
    void isPostUpdated_WithNullCreatedAt_ShouldReturnFalse() {
        // Arrange
        Post post = new Post();
        post.setCreatedAt(null);
        post.setUpdatedAt(OffsetDateTime.now());

        // Act
        Boolean result = postMapper.isPostUpdated(post);

        // Assert
        assertFalse(result);
    }

    @Test
    void isPostUpdated_WithNullUpdatedAt_ShouldReturnFalse() {
        // Arrange
        Post post = new Post();
        post.setCreatedAt(OffsetDateTime.now());
        post.setUpdatedAt(null);

        // Act
        Boolean result = postMapper.isPostUpdated(post);

        // Assert
        assertFalse(result);
    }

    @Test
    void isPostUpdated_WithBothNullDates_ShouldReturnFalse() {
        // Arrange
        Post post = new Post();
        post.setCreatedAt(null);
        post.setUpdatedAt(null);

        // Act
        Boolean result = postMapper.isPostUpdated(post);

        // Assert
        assertFalse(result);
    }
}
