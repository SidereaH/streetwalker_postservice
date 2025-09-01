package streetwalker.postservice.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import streetwalker.postservice.dto.comment.CommentCreateDTO;
import streetwalker.postservice.dto.post.PostCreateDTO;
import streetwalker.postservice.dto.post.PostDTO;
import streetwalker.postservice.dto.post.PostUpdateDTO;
import streetwalker.postservice.dto.postlike.PostLikeDTO;
import streetwalker.postservice.models.Comment;
import streetwalker.postservice.services.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateDTO postDTO) {
        try {
            PostDTO created = postService.create(postDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.getPost(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getPosts(Pageable pageable, @RequestParam(required = false, defaultValue = "") String title) {
        try {
            Page<PostDTO> posts = postService.getPosts(pageable, title);
            return ResponseEntity.ok(posts);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updatePost(@RequestBody PostUpdateDTO postDTO) {
        try {
            return ResponseEntity.ok(postService.update(postDTO));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/like")
    public ResponseEntity<?> likePost(@RequestBody PostLikeDTO postLikeDTO) {
        try {
            postService.likePost(postLikeDTO);
            return ResponseEntity.ok("Post liked");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/unlike")
    public ResponseEntity<?> unlikePost(@RequestBody PostLikeDTO postLikeDTO) {
        try {
            postService.unlikePost(postLikeDTO);
            return ResponseEntity.ok("Post unliked");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentCreateDTO commentDTO) {
        try {
            commentDTO.setPostId(postId);
            Comment comment = postService.addComment(commentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
