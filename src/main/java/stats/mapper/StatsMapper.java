package stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import stats.dto.StatsDto;
import stats.model.Stats;

@Mapper(uses = MemberMapper.class)
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    @Mapping(target = "ranksPerDayPerPart", ignore = true)
    @Mapping(target = "rankTillDay", ignore = true)
    @Mapping(target = "days", ignore = true)
    @Mapping(target = "sortedMembers", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    Stats map(StatsDto dto);
}
