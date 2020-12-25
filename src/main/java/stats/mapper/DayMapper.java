package stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stats.dto.DayDto;
import stats.model.Day;

@Mapper(uses = StarMapper.class)
public interface DayMapper {

    @Mapping(target = "stars", ignore = true)
    @Mapping(target = "dayRank", ignore = true)
    @Mapping(target = "dayPoints", ignore = true)
    @Mapping(target = "dayChange", ignore = true)
    Day map(DayDto dto);
}
