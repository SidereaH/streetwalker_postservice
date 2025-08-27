package streetwalker.postservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import streetwalker.postservice.dto.category.CategoryDTO;
import streetwalker.postservice.dto.comment.CommentCreateDTO;
import streetwalker.postservice.dto.post.PostCreateDTO;
import streetwalker.postservice.dto.post.PostDTO;
import streetwalker.postservice.dto.post.PostUpdateDTO;
import streetwalker.postservice.dto.postlike.PostLikeDTO;
import streetwalker.postservice.dto.tag.TagDTO;
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

    @Mock CommentService commentService;

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

    @Test
    void getPost_WithExistingId_ShouldReturnPostDTO() {
        // Arrange
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        PostDTO expectedDTO = new PostDTO();
        expectedDTO.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDTO(post)).thenReturn(expectedDTO);

        // Act
        PostDTO result = postService.getPost(postId);

        // Assert
        assertNotNull(result);
        assertEquals(postId, result.getId());
        verify(postRepository).findById(postId);
        verify(postMapper).toDTO(post);
    }

    @Test
    void getPost_WithNonExistingId_ShouldThrowException() {
        // Arrange
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.getPost(postId);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postMapper, never()).toDTO(any());
    }

    @Test
    void getPosts_WithTitle_ShouldReturnPageOfPostDTO() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        String title = "test";

        Post post1 = new Post();
        post1.setId(1L);
        Post post2 = new Post();
        post2.setId(2L);

        List<Post> posts = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        PostDTO dto1 = new PostDTO();
        dto1.setId(1L);
        PostDTO dto2 = new PostDTO();
        dto2.setId(2L);

        when(postRepository.findPostByTitleContainingIgnoreCase(pageable, title))
                .thenReturn(postPage);
        when(postMapper.toDTO(post1)).thenReturn(dto1);
        when(postMapper.toDTO(post2)).thenReturn(dto2);

        // Act
        Page<PostDTO> result = postService.getPosts(pageable, title);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
        verify(postRepository).findPostByTitleContainingIgnoreCase(pageable, title);
        verify(postMapper, times(2)).toDTO(any(Post.class));
    }

    @Test
    void getPosts_WithEmptyTitle_ShouldReturnAllPosts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String title = "";

        Post post = new Post();
        post.setId(1L);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);
        PostDTO dto = new PostDTO();
        dto.setId(1L);

        when(postRepository.findPostByTitleContainingIgnoreCase(pageable, title))
                .thenReturn(postPage);
        when(postMapper.toDTO(post)).thenReturn(dto);

        // Act
        Page<PostDTO> result = postService.getPosts(pageable, title);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(postRepository).findPostByTitleContainingIgnoreCase(pageable, title);
    }

    @Test
    void getPosts_WithNullTitle_ShouldReturnAllPosts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String title = null;

        Post post = new Post();
        post.setId(1L);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);
        PostDTO dto = new PostDTO();
        dto.setId(1L);

        when(postRepository.findPostByTitleContainingIgnoreCase(pageable, title))
                .thenReturn(postPage);
        when(postMapper.toDTO(post)).thenReturn(dto);

        // Act
        Page<PostDTO> result = postService.getPosts(pageable, title);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(postRepository).findPostByTitleContainingIgnoreCase(pageable, title);
    }

    @Test
    void getPosts_WithEmptyResult_ShouldReturnEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String title = "nonexistent";

        Page<Post> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(postRepository.findPostByTitleContainingIgnoreCase(pageable, title))
                .thenReturn(emptyPage);

        // Act
        Page<PostDTO> result = postService.getPosts(pageable, title);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(postRepository).findPostByTitleContainingIgnoreCase(pageable, title);
        verify(postMapper, never()).toDTO(any());
    }

    @Test
    void update_WithExistingPost_ShouldUpdateAndReturnDTO() {
        // Arrange
        Long postId = 1L;
        PostUpdateDTO updateDTO = new PostUpdateDTO();
        updateDTO.setId(postId);
        updateDTO.setTitle("Updated Title");
        updateDTO.setContent("Updated Content");

        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setTitle("Old Title");
        existingPost.setContent("Old Content");

        Post savedPost = new Post();
        savedPost.setId(postId);
        savedPost.setTitle("Updated Title");
        savedPost.setContent("Updated Content");

        PostDTO expectedDTO = new PostDTO();
        expectedDTO.setId(postId);
        expectedDTO.setTitle("Updated Title");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(savedPost);
        when(postMapper.toDTO(savedPost)).thenReturn(expectedDTO);

        // Act
        PostDTO result = postService.update(updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Updated Title", result.getTitle());

        verify(postRepository).findById(postId);
        verify(postMapper).updateFromDTO(updateDTO, existingPost);
        verify(postRepository).save(existingPost);
        verify(postMapper).toDTO(savedPost);
    }

    @Test
    void update_WithNonExistingPost_ShouldThrowException() {
        // Arrange
        Long postId = 999L;
        PostUpdateDTO updateDTO = new PostUpdateDTO();
        updateDTO.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.update(updateDTO);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postMapper, never()).updateFromDTO(any(), any());
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toDTO(any());
    }

    @Test
    void update_WithNullDTO_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            postService.update(null);
        });

        verify(postRepository, never()).findById(any());
    }

    @Test
    void update_WithNullIdInDTO_ShouldThrowException() {
        // Arrange
        PostUpdateDTO updateDTO = new PostUpdateDTO();
        updateDTO.setId(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.update(updateDTO);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).findById(null);
    }

    @Test
    void delete_WithExistingId_ShouldDeletePost() {
        // Arrange
        Long postId = 1L;
        doNothing().when(postRepository).deleteById(postId);

        // Act
        assertDoesNotThrow(() -> {
            postService.delete(postId);
        });

        // Assert
        verify(postRepository).deleteById(postId);
    }

    @Test
    void delete_WithNonExistingId_ShouldThrowDataAccessException() {
        // Arrange
        Long postId = 999L;
        doThrow(new DataAccessException("Post not found") {}).when(postRepository).deleteById(postId);

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            postService.delete(postId);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).deleteById(postId);
    }

    @Test
    void delete_WithNullId_ShouldThrowException() {
        // Arrange
        Long postId = null;
        doThrow(new IllegalArgumentException("ID must not be null")).when(postRepository).deleteById(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            postService.delete(postId);
        });

        verify(postRepository).deleteById(null);
    }

    // Дополнительные тесты для edge cases

    @Test
    void getPosts_WithDifferentPageable_ShouldUseCorrectPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 5, Sort.by("title").descending());
        String title = "test";

        Page<Post> postPage = new PageImpl<>(List.of(), pageable, 0);

        when(postRepository.findPostByTitleContainingIgnoreCase(pageable, title))
                .thenReturn(postPage);

        // Act
        Page<PostDTO> result = postService.getPosts(pageable, title);

        // Assert
        assertNotNull(result);
        verify(postRepository).findPostByTitleContainingIgnoreCase(pageable, title);
    }

    @Test
    void update_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        Long postId = 1L;
        PostUpdateDTO updateDTO = new PostUpdateDTO();
        updateDTO.setId(postId);
        updateDTO.setTitle("New Title");
        // content не установлен - должен остаться прежним

        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setTitle("Old Title");
        existingPost.setContent("Old Content");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(existingPost);
        when(postMapper.toDTO(existingPost)).thenReturn(new PostDTO());

        // Act
        postService.update(updateDTO);

        // Assert
        verify(postMapper).updateFromDTO(updateDTO, existingPost);
        // Маппер должен обработать частичное обновление
    }

    // ======================== addComment ========================
    @Test
    void addComment_WithValidData_ShouldCreateComment() {
        CommentCreateDTO commentDTO = new CommentCreateDTO();
        commentDTO.setPostId(1L);
        commentDTO.setAuthorId(1L);
        commentDTO.setContent("Test comment");

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Old Title");
        existingPost.setContent("Old Content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        postService.addComment(commentDTO);
        verify(commentService).createComment(commentDTO, existingPost );
    }
    @Test
    void addComment_WithInvalidPostId_ShouldThrowException() {
        CommentCreateDTO commentDTO = new CommentCreateDTO();
        commentDTO.setAuthorId(1L);
        commentDTO.setContent("Test comment");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.addComment(commentDTO);
        });

        assertEquals("Post not found", exception.getMessage());
    }
}