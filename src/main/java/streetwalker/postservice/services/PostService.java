package streetwalker.postservice.services;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import streetwalker.postservice.dto.PostCreateDTO;
import streetwalker.postservice.dto.PostDTO;
import streetwalker.postservice.dto.PostLikeDTO;
import streetwalker.postservice.dto.PostUpdateDTO;
import streetwalker.postservice.mappers.PostMapper;
import streetwalker.postservice.models.*;
import streetwalker.postservice.repositories.PostRepository;

import java.util.List;
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
    public PostDTO getPost(Long id) throws RuntimeException {
        return postMapper.toDTO(postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found")));
    }
    //связать с юзерсервисом, добавить поиск по сообществам и юзернеймам
    public Page<PostDTO> getPosts(Pageable pageable, String title) throws RuntimeException {
        return postRepository.findPostByTitleContainingIgnoreCase(pageable, title).map(postMapper::toDTO);
    }

    public PostDTO update(PostUpdateDTO postDTO) throws RuntimeException {
        Post post = postRepository.findById(postDTO.getId()).orElseThrow(()-> new RuntimeException("Post not found"));
        postMapper.updateFromDTO(postDTO, post);
        post.setTags(tagService.proceedTagsWhenCreatingPost(postDTO.getTags()));
        post.setCategory(categoryService.getCategory( postDTO.getCategoryName()));
        return postMapper.toDTO(postRepository.save(post));
    }
    public void delete(Long id) throws DataAccessException {
        postRepository.deleteById(id);
    }
    public void likePost(PostLikeDTO postLikeDTO) throws RuntimeException {
        Post post = postRepository.findById(postLikeDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getId().getAuthorId().equals(postLikeDTO.getAuthorId()));

        if (alreadyLiked) {
            throw new RuntimeException("Already liked");
        }

        PostLike newLike = new PostLike(post, postLikeDTO.getAuthorId());
        post.getLikes().add(newLike);
        postRepository.save(post);
    }

    public void unlikePost(PostLikeDTO postLikeDTO) throws RuntimeException {
        Post post = postRepository.findById(postLikeDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

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
