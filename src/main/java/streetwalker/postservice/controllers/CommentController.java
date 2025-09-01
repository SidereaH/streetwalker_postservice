package streetwalker.postservice.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import streetwalker.postservice.dto.comment.CommentCreateDTO;
import streetwalker.postservice.dto.comment.CommentUpdateDTO;
import streetwalker.postservice.models.Comment;
import streetwalker.postservice.services.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentCreateDTO commentCreateDTO) {
        try {
            // post для комментария должен подтягиваться в PostService
            return ResponseEntity.status(HttpStatus.CREATED).body("Use /api/posts/{postId}/comments instead");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateComment(@RequestBody CommentUpdateDTO commentUpdateDTO) {
        try {
            Comment updated = commentService.updateComment(commentUpdateDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
