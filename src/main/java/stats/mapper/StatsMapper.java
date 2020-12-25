package stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import stats.dto.StatsDto;
import stats.model.Stats;

@Mapper(uses = MemberMapper.class)
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);
    Stats map(StatsDto dto);
}
