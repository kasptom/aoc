package stats.mapper;

import org.mapstruct.Mapper;
import stats.dto.DayDto;
import stats.model.Day;

@Mapper(uses = StarMapper.class)
public interface DayMapper {
    Day map(DayDto dto);
}
