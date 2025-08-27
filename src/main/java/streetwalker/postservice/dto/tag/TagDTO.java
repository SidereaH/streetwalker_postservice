package streetwalker.postservice.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class TagDTO {
    private String name;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDTO tagDTO = (TagDTO) o;
        return Objects.equals(name, tagDTO.name) && Objects.equals(description, tagDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
