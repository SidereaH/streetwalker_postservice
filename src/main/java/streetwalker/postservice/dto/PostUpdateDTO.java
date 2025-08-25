package streetwalker.postservice.dto;

import lombok.Getter;
import lombok.Setter;
import streetwalker.postservice.models.Category;
import streetwalker.postservice.models.Tag;

import java.util.List;
@Getter
@Setter
public class PostUpdateDTO {

    private Long id;
    private String title;
    private String content;
    private List<String> tags;
    private String categoryName;
}
