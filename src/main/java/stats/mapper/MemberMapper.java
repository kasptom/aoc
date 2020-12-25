package stats.mapper;

import org.mapstruct.Mapper;
import stats.dto.DayDto;
import stats.model.Day;

@Mapper(uses = DayMapper.class)
public interface MemberMapper {
    Day map(DayDto dto);
}
