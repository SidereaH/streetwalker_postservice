package streetwalker.postservice.mappers;

import org.mapstruct.*;
import streetwalker.postservice.dto.category.CategoryDTO;
import streetwalker.postservice.models.Category;

@Mapper(        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category toCategory(CategoryDTO categoryDTO);
}
