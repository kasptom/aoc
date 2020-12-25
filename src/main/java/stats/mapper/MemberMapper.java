package stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stats.dto.MemberDto;
import stats.model.Member;

@Mapper(uses = DayMapper.class)
public interface MemberMapper {

    @Mapping(target = "scoreHistory", ignore = true)
    @Mapping(target = "daysRanks", ignore = true)
    @Mapping(target = "dayPoints", ignore = true)
    @Mapping(target = "tillDayRanks", ignore = true)
    Member map(MemberDto dto);
}
