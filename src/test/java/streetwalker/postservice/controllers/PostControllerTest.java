package streetwalker.postservice.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import streetwalker.postservice.dto.comment.CommentCreateDTO;
import streetwalker.postservice.dto.post.PostCreateDTO;
import streetwalker.postservice.dto.post.PostDTO;
import streetwalker.postservice.dto.post.PostUpdateDTO;
import streetwalker.postservice.dto.postlike.PostLikeDTO;
import streetwalker.postservice.models.Comment;
import streetwalker.postservice.services.PostService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void createPost_Success() throws Exception {
        PostDTO dto = new PostDTO();
        dto.setTitle("Post1");
        when(postService.create(any(PostCreateDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Post1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Post1"));
    }

    @Test
    void createPost_Failure() throws Exception {
        when(postService.create(any())).thenThrow(new RuntimeException("Bad post"));

        mockMvc.perform(post("/api/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad post"));
    }

    @Test
    void getPost_Success() throws Exception {
        PostDTO dto = new PostDTO();
        dto.setTitle("MyPost");
        when(postService.getPost(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/posts/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("MyPost"));
    }

    @Test
    void getPost_NotFound() throws Exception {
        when(postService.getPost(1L)).thenThrow(new RuntimeException("Post not found"));

        mockMvc.perform(get("/api/posts/1").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post not found"));
    }

    @Test
    void getPosts_Success() throws Exception {
        PostDTO dto = new PostDTO();
        dto.setTitle("PagedPost");
        Page<PostDTO> page = new PageImpl<>(List.of(dto));

        when(postService.getPosts(any(Pageable.class), Mockito.eq(""))).thenReturn(page);

        mockMvc.perform(get("/api/posts").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("PagedPost"));
    }

    @Test
    void updatePost_Success() throws Exception {
        PostDTO dto = new PostDTO();
        dto.setTitle("Updated");
        when(postService.update(any(PostUpdateDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"title\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updatePost_NotFound() throws Exception {
        when(postService.update(any())).thenThrow(new RuntimeException("Post not found"));

        mockMvc.perform(put("/api/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"title\":\"Updated\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post not found"));
    }

    @Test
    void deletePost_Success() throws Exception {
        mockMvc.perform(delete("/api/posts/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_NotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Not found")).when(postService).delete(1L);

        mockMvc.perform(delete("/api/posts/1").with(csrf()))

                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }

    @Test
    void likePost_Success() throws Exception {
        mockMvc.perform(post("/api/posts/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":1,\"authorId\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Post liked"));
    }

    @Test
    void likePost_AlreadyLiked() throws Exception {
        Mockito.doThrow(new RuntimeException("Already liked")).when(postService).likePost(any(PostLikeDTO.class));

        mockMvc.perform(post("/api/posts/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":1,\"authorId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Already liked"));
    }

    @Test
    void unlikePost_Success() throws Exception {
        mockMvc.perform(post("/api/posts/unlike")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":1,\"authorId\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Post unliked"));
    }

    @Test
    void unlikePost_NotLiked() throws Exception {
        Mockito.doThrow(new RuntimeException("Already unliked")).when(postService).unlikePost(any(PostLikeDTO.class));

        mockMvc.perform(post("/api/posts/unlike")
                .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":1,\"authorId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Already unliked"));
    }

    @Test
    void addComment_Success() throws Exception {
        Comment comment = new Comment();
        comment.setContent("New comment");
        when(postService.addComment(any(CommentCreateDTO.class))).thenReturn(comment);

        mockMvc.perform(post("/api/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":1,\"content\":\"New comment\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("New comment"));
    }

    @Test
    void addComment_PostNotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Post not found")).when(postService).addComment(any(CommentCreateDTO.class));

        mockMvc.perform(post("/api/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":1,\"content\":\"New comment\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post not found"));
    }
}
