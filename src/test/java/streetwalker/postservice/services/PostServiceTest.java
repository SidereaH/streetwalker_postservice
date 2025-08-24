package streetwalker.postservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import streetwalker.postservice.dto.*;
import streetwalker.postservice.mappers.PostMapper;
import streetwalker.postservice.models.*;
import streetwalker.postservice.repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;


    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostService postService;

    @Test
    void create_WithNullPostDTO_ShouldReturnNull() {
        // Act
        PostDTO result = postService.create(null);

        // Assert
        assertNull(result);
    }

    @Test
    void create_WithValidPostDTO_ShouldCreateAndReturnPostDTO() {
        // Arrange
        PostCreateDTO postCreateDTO = new PostCreateDTO();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("Test Category");
        postCreateDTO.setCategory(categoryDTO);
        List<TagDTO> tags = new ArrayList<>();
        postCreateDTO.setTags(tags.stream().map(tagDTO -> tagDTO.getName()).toList());

        Post post = new Post();
        Category category = new Category();
        category.setCategoryName("Test Category");

        PostDTO expectedPostDTO = new PostDTO();

        when(postMapper.fromCreateDTO(postCreateDTO)).thenReturn(post);
        when(categoryService.getCategory("Test Category")).thenReturn(category);
        when(tagService.proceedTagsWhenCreatingPost(tags.stream().map(tagDTO -> tagDTO.getName()).toList())).thenReturn(new ArrayList<>());
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDTO(post)).thenReturn(expectedPostDTO);

        // Act
        PostDTO result = postService.create(postCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedPostDTO, result);
        assertEquals(category, post.getCategory());
        verify(postMapper).fromCreateDTO(postCreateDTO);
        verify(categoryService).getCategory("Test Category");
        verify(tagService).proceedTagsWhenCreatingPost(tags.stream().map(tagDTO -> tagDTO.getName()).toList());
        verify(postRepository).save(post);
        verify(postMapper).toDTO(post);
    }

    @Test
    void likePost_WithExistingPost_ShouldAddLike() {
        // Arrange
        Long postId = 1L;
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setPostId(postId);

        Post post = new Post();
        post.setId(postId);
        post.setLikes(new ArrayList<>());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        // Act
        postService.likePost(postLikeDTO);

        // Assert
        assertEquals(1, post.getLikes().size());
        assertTrue(post.getLikes().stream().anyMatch(like -> like.getPost().equals(post)));
        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
    }

    @Test
    void likePost_WithNonExistingPost_ShouldThrowException() {
        // Arrange
        Long postId = 999L;
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setPostId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.likePost(postLikeDTO);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any());
    }

    @Test
    void likePost_WhenPostAlreadyLiked_ShouldThrowException() {
        // Arrange
        Long postId = 1L;
        Long authorId = 100L;
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setPostId(postId);
        postLikeDTO.setAuthorId(authorId);

        Post post = new Post();
        post.setId(postId);
        List<PostLike> likes = new ArrayList<>();

        // Создаем лайк с правильным LikeId
        PostLike existingLike = new PostLike();
        existingLike.setId(new LikeId(postId, authorId)); // Устанавливаем id напрямую
        existingLike.setPost(post);
        likes.add(existingLike);
        post.setLikes(likes);

        when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(post));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.likePost(postLikeDTO);
        });

        assertEquals("Already liked", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any());
    }

    @Test
    void unlikePost_WithExistingPostAndLike_ShouldRemoveLike() {
        // Arrange
        Long postId = 1L;
        Long authorId = 100L;
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setPostId(postId);
        postLikeDTO.setAuthorId(authorId);

        Post post = new Post();
        post.setId(postId);
        List<PostLike> likes = new ArrayList<>();

        // Создаем лайк с правильным LikeId
        PostLike likeToRemove = new PostLike();
        likeToRemove.setId(new LikeId(postId, authorId)); // Устанавливаем id напрямую
        likeToRemove.setPost(post);
        likes.add(likeToRemove);
        post.setLikes(likes);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        // Act
        postService.unlikePost(postLikeDTO);

        // Assert
        assertTrue(post.getLikes().isEmpty());
        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
    }
    @Test
    void unlikePost_WhenPostNotLikedByUser_ShouldThrowException() {
        // Arrange
        Long postId = 1L;
        Long authorId = 100L;
        Long differentAuthorId = 200L; // Другой автор
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setPostId(postId);
        postLikeDTO.setAuthorId(authorId);

        Post post = new Post();
        post.setId(postId);
        List<PostLike> likes = new ArrayList<>();

        // Лайк от другого автора
        PostLike existingLike = new PostLike(post, differentAuthorId);
        likes.add(existingLike);
        post.setLikes(likes);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.unlikePost(postLikeDTO);
        });

        assertEquals("Already unliked or never liked", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any());
    }
    @Test
    void unlikePost_WithNonExistingPost_ShouldThrowException() {
        // Arrange
        Long postId = 999L;
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setPostId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.unlikePost(postLikeDTO);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any());
    }

    @Test
    void unlikePost_WhenPostNotLiked_ShouldThrowException() {
        // Arrange
        Long postId = 1L;
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setPostId(postId);

        Post post = new Post();
        post.setId(postId);
        post.setLikes(new ArrayList<>()); // Empty likes list

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.unlikePost(postLikeDTO);
        });

        assertEquals("Already unliked or never liked", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any());
    }
}