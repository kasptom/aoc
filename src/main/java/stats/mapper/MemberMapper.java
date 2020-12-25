package stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stats.dto.DayDto;
import stats.model.Day;

@Mapper(uses = DayMapper.class)
public interface MemberMapper {
    @Mapping(target = "stars", ignore = true)
    @Mapping(target = "dayRank", ignore = true)
    @Mapping(target = "dayPoints", ignore = true)
    @Mapping(target = "dayChange", ignore = true)
    Day map(DayDto dto);
}
