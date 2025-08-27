package streetwalker.postservice.services;

import org.springframework.stereotype.Service;
import streetwalker.postservice.dto.tag.TagDTO;
import streetwalker.postservice.models.Tag;
import streetwalker.postservice.repositories.TagRepository;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    public List<Tag> proceedTagsWhenCreatingPost(List<String> tags) {
        if (tags == null) return null;
        return tags.stream().map(tagName -> tagRepository.findTagByTagName(tagName).orElseGet(() -> tagRepository.save(new Tag(null, tagName, "description")))).toList();
    }
    public TagDTO convertToDTO(Tag tag) {
        if (tag == null) return null;
        return new TagDTO(tag.getTagName(), tag.getTagDescription());

    }
}
