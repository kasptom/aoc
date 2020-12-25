package stats.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static stats.factory.StatsFactory.AOC_EST_ZONE;

@Getter
@Setter
public class Day {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Star first;
    private Star second;

    private List<Star> stars = new ArrayList<>();
    private List<Integer> dayRank = new ArrayList<>();
    private List<Integer> dayPoints = new ArrayList<>();
    private int dayChange = 0;

    int getStarsCount() {
        if (second != null) return 2;
        return first != null ? 1 : 0;
    }

    public String getTimestampStr(int partIdx) {
        if (stars.size() <= partIdx) {
            return "N/A";
        }
        var star = stars.get(partIdx);
        if (star == null) {
            return "N/A";
        }
        long timestamp = star.getTimestamp();
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of("+01:00")).format(DATE_TIME_FORMATTER);
    }

    public ZonedDateTime getDateTime(int partIdx) {
        var star = stars.get(partIdx);
        long timestamp = star.getTimestamp();
        return LocalDateTime.ofEpochSecond(timestamp, 0, AOC_EST_ZONE).atZone(AOC_EST_ZONE);
    }
}
