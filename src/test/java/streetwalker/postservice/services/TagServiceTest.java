package streetwalker.postservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import streetwalker.postservice.dto.tag.TagDTO;
import streetwalker.postservice.models.Tag;
import streetwalker.postservice.repositories.TagRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {
    @Mock
    private TagRepository tagRepository;
    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void proceedTagsWhenCreatingPost_ReturnsExistingAndNewTags() {
        when(tagRepository.findTagByTagName("java")).thenReturn(Optional.of(new Tag(1L, "java", "desc")));
        when(tagRepository.findTagByTagName("spring")).thenReturn(Optional.empty());
        when(tagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Tag> tags = tagService.proceedTagsWhenCreatingPost(List.of("java", "spring"));

        assertEquals(2, tags.size());
        verify(tagRepository, times(2)).findTagByTagName(anyString());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void proceedTagsWhenCreatingPost_ReturnsNull_WhenInputIsNull() {
        assertNull(tagService.proceedTagsWhenCreatingPost(null));
    }
    @Test
    void convertToDTO_ReturnNull() {
        TagDTO dto = tagService.convertToDTO(null);
        assertNull(dto);
    }
    @Test
    void convertToDTO_ReturnValidTagDTO() {
        TagDTO rightDto = new TagDTO("java", "desc");
        TagDTO dto = tagService.convertToDTO(new Tag(1L, "java", "desc"));

        assertEquals(rightDto, dto);
    }
}
