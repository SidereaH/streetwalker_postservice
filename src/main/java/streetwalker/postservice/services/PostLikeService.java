package streetwalker.postservice.services;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import streetwalker.postservice.models.PostLike;
import streetwalker.postservice.models.PostLikeId;
import streetwalker.postservice.repositories.PostLikeRepository;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;

    public PostLikeService(PostLikeRepository postLikeRepository) {
        this.postLikeRepository = postLikeRepository;
    }

    public PostLike likePost(Long postId, Long authorId) throws DataAccessException {
        return postLikeRepository.save(new PostLike(new PostLikeId(postId, authorId)));
    }
}
