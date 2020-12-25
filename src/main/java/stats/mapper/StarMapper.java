package stats.mapper;

import org.mapstruct.Mapper;
import stats.dto.StarDto;
import stats.model.Star;

@Mapper
public interface StarMapper {
    Star map(StarDto dto);
}
