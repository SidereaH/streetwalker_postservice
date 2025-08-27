package streetwalker.postservice.services;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import streetwalker.postservice.dto.comment.CommentCreateDTO;
import streetwalker.postservice.dto.comment.CommentUpdateDTO;
import streetwalker.postservice.models.Comment;
import streetwalker.postservice.models.Post;
import streetwalker.postservice.repositories.CommentRepository;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;


    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(CommentCreateDTO commentCreateDTO, Post post) {
        Comment newComment = new Comment();
        newComment.setAuthorId(commentCreateDTO.getAuthorId());
        newComment.setContent(commentCreateDTO.getContent());
        newComment.setPost(post);

        if (commentCreateDTO.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentCreateDTO.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            newComment.setParentComment(parentComment);
        }

        return commentRepository.save(newComment);
    }
    public Comment updateComment(CommentUpdateDTO commentUpdateDTO) {
        Comment comment = commentRepository.findById(commentUpdateDTO.getCommentId()).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(commentUpdateDTO.getNewContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.deleteById(commentId);
    }
}