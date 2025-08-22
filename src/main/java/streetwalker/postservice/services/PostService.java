package streetwalker.postservice.services;

import org.springframework.stereotype.Service;
import streetwalker.postservice.dto.PostCreateDTO;
import streetwalker.postservice.dto.PostDTO;
import streetwalker.postservice.dto.PostLikeDTO;
import streetwalker.postservice.mappers.PostMapper;
import streetwalker.postservice.models.Post;
import streetwalker.postservice.models.PostLike;
import streetwalker.postservice.repositories.PostRepository;

import java.util.List;


@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostLikeService postLikeService;

    public PostService(PostRepository postRepository, PostMapper postMapper, PostLikeService postLikeService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.postLikeService = postLikeService;
    }
    //добавить обработку ошибок и корректность полей
    public PostDTO create(PostCreateDTO postDTO) throws RuntimeException {
        if (postDTO != null){
            Post post = postMapper.map(postDTO);
            postRepository.save(post);
            return postMapper.map(post);
        }
        return null;

    }
    public void likePost(PostLikeDTO postLikeDTO) throws RuntimeException {
        Post post = postRepository.findById(postLikeDTO.getPostId()).orElseThrow(() -> new RuntimeException("Post not found"));
        post.getLikes().add(postLikeService.likePost(postLikeDTO.getPostId(), postLikeDTO.getAuthorId()));
        postRepository.save(post);
    }


}
