package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Day {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @JsonProperty("1")
    Star first;

    @JsonProperty("2")
    Star second;

    List<Star> stars = new ArrayList<>();

    int getStarsCount() {
        if (second != null) return 2;
        return first != null ? 1 : 0;
    }

    public String getTime(int part) {
        stars.clear();
        stars.add(first); stars.add(second);
        var star = stars.get(part - 1);
        if (star == null) {
            return "N/A";
        }
        long timestamp = star.timestamp;
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of("+01:00")).format(DATE_TIME_FORMATTER);
    }
}
