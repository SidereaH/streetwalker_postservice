package streetwalker.postservice.dto.post;

import lombok.Getter;
import lombok.Setter;

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
