package streetwalker.postservice.services;

import org.springframework.stereotype.Service;
import streetwalker.postservice.dto.PostCreateDTO;
import streetwalker.postservice.dto.PostDTO;
import streetwalker.postservice.dto.PostLikeDTO;
import streetwalker.postservice.mappers.PostMapper;
import streetwalker.postservice.models.*;
import streetwalker.postservice.repositories.PostRepository;

import java.util.Optional;


@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CategoryService categoryService;
    private final TagService tagService;

    public PostService(PostRepository postRepository, PostMapper postMapper, CategoryService categoryService, TagService tagService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }
    //добавить обработку ошибок и корректность полей
    public PostDTO create(PostCreateDTO postDTO) throws RuntimeException {
        if (postDTO != null){
            Post post = postMapper.fromCreateDTO(postDTO);
            Category category =  categoryService.getCategory(postDTO.getCategory().getCategoryName());
            post.setCategory(category);
            post.setTags(tagService.proceedTagsWhenCreatingPost(postDTO.getTags()));


            postRepository.save(post);
            return postMapper.toDTO(post);
        }
        return null;

    }
    public void likePost(PostLikeDTO postLikeDTO) throws RuntimeException {
        Post post = postRepository.findById(postLikeDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Проверяем существование лайка по автору и посту
        boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getId().getAuthorId().equals(postLikeDTO.getAuthorId()));

        if (alreadyLiked) {
            throw new RuntimeException("Already liked");
        }

        // Создаем лайк только после проверки
        PostLike newLike = new PostLike(post, postLikeDTO.getAuthorId());
        post.getLikes().add(newLike);
        postRepository.save(post);
    }

    public void unlikePost(PostLikeDTO postLikeDTO) throws RuntimeException {
        Post post = postRepository.findById(postLikeDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Ищем лайк по автору
        Optional<PostLike> likeToRemove = post.getLikes().stream()
                .filter(like -> like.getId().getAuthorId().equals(postLikeDTO.getAuthorId()))
                .findFirst();

        if (likeToRemove.isEmpty()) {
            throw new RuntimeException("Already unliked or never liked");
        }

        post.getLikes().remove(likeToRemove.get());
        postRepository.save(post);
    }

}
