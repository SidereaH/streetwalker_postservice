package streetwalker.postservice.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import streetwalker.postservice.controllers.CommentController;
import streetwalker.postservice.dto.comment.CommentUpdateDTO;
import streetwalker.postservice.models.Comment;
import streetwalker.postservice.services.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void createComment_AlwaysReturnsRedirectMessage() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":1,\"content\":\"Hi\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Use /api/posts/{postId}/comments instead"));
    }

    @Test
    void updateComment_Success() throws Exception {
        Comment updated = new Comment();
        updated.setId(1L);
        updated.setContent("Updated text");
        when(commentService.updateComment(any(CommentUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commentId\":1,\"newContent\":\"Updated text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated text"));
    }

    @Test
    void updateComment_NotFound() throws Exception {
        when(commentService.updateComment(any())).thenThrow(new RuntimeException("Comment not found"));

        mockMvc.perform(put("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commentId\":1,\"newContent\":\"Updated text\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment not found"));
    }

    @Test
    void deleteComment_Success() throws Exception {
        mockMvc.perform(delete("/api/comments/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_NotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Comment not found")).when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/1").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment not found"));
    }
}
