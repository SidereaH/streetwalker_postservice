package streetwalker.postservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import streetwalker.postservice.dto.comment.CommentCreateDTO;
import streetwalker.postservice.dto.comment.CommentUpdateDTO;
import streetwalker.postservice.models.Comment;
import streetwalker.postservice.models.Post;
import streetwalker.postservice.repositories.CommentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService service;

    // ==================== createComment ====================

    @Test
    void createComment_withoutParent_shouldSaveCorrectComment() {
        CommentCreateDTO commentDTO = new CommentCreateDTO();
        commentDTO.setPostId(1L);
        commentDTO.setAuthorId(10L);
        commentDTO.setContent("Test comment");

        Post post = new Post();
        post.setId(1L);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment saved = service.createComment(commentDTO, post);

        verify(commentRepository).save(captor.capture());
        Comment captured = captor.getValue();

        assertEquals(commentDTO.getAuthorId(), captured.getAuthorId());
        assertEquals(commentDTO.getContent(), captured.getContent());
        assertEquals(post, captured.getPost());
        assertNull(captured.getParentComment());

        // проверяем возвращаемое значение
        assertEquals(captured, saved);
    }

    @Test
    void createComment_withParent_shouldSaveCorrectComment() {
        Comment parentComment = new Comment();
        parentComment.setId(1L);
        parentComment.setAuthorId(2L);
        parentComment.setContent("Parent comment");

        CommentCreateDTO commentDTO = new CommentCreateDTO();
        commentDTO.setParentCommentId(1L);
        commentDTO.setPostId(2L);
        commentDTO.setAuthorId(3L);
        commentDTO.setContent("Child comment");

        Post post = new Post();
        post.setId(2L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment saved = service.createComment(commentDTO, post);

        assertEquals(commentDTO.getAuthorId(), saved.getAuthorId());
        assertEquals(commentDTO.getContent(), saved.getContent());
        assertEquals(post, saved.getPost());
        assertEquals(parentComment, saved.getParentComment());
    }

    @Test
    void createComment_withInvalidParent_shouldThrow() {
        CommentCreateDTO commentDTO = new CommentCreateDTO();
        commentDTO.setParentCommentId(99L);
        commentDTO.setAuthorId(5L);
        commentDTO.setContent("Child comment");

        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.createComment(commentDTO, new Post())
        );
        assertEquals("Parent comment not found", ex.getMessage());
    }

    // ==================== updateComment ====================

    @Test
    void updateComment_shouldReturnUpdatedComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthorId(2L);
        comment.setContent("Old content");

        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setCommentId(1L);
        dto.setNewContent("New content");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment updated = service.updateComment(dto);

        assertEquals("New content", updated.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    void updateComment_shouldThrowIfNotFound() {
        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setCommentId(42L);

        when(commentRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.updateComment(dto)
        );
        assertEquals("Comment not found", ex.getMessage());
    }

    // ==================== deleteComment ====================

    @Test
    void deleteComment_shouldDeleteIfExists() {
        Comment comment = new Comment();
        comment.setId(1L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        service.deleteComment(1L);

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteComment_shouldThrowIfNotFound() {
        when(commentRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.deleteComment(123L)
        );
        assertEquals("Comment not found", ex.getMessage());
    }
}
