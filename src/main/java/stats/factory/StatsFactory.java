package stats.factory;

import stats.dto.StatsDto;
import stats.mapper.StatsMapper;
import stats.model.Stats;

public class StatsFactory {
    public static Stats create(StatsDto statsDto) {
        Stats stats = StatsMapper.INSTANCE.map(statsDto);
        stats.updateMembers(stats.getMembers());
        stats.updateOwnerName(stats.getOwnerId());

        return stats;
    }

    private StatsFactory() {
    }
}
