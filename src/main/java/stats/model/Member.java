package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class Member {
    static final String MEMBER_ROW_FORMAT = "%-25s %12d %12d";

    @JsonProperty
    long id;

    @JsonProperty
    int stars;

    @JsonProperty
    String name;

    @JsonProperty("local_score")
    int localScore;

    @JsonProperty("global_score")
    long globalScore;

    @JsonProperty("last_star_ts")
    long lastStartTimestamp;

    @JsonProperty("completion_day_level")
    HashMap<String, Day> completionDayLevel;

    public int getStarsForDay(int dayIdx) {
        if (!completionDayLevel.containsKey(String.valueOf(dayIdx))) {
            return 0;
        }
        Day day = completionDayLevel.get(String.valueOf(dayIdx));
        return day.getStarsCount();
    }

    public String getTime(int dayIdx, int part) {
        var day = completionDayLevel.get(String.valueOf(dayIdx));
        return day == null ? "N/A" : day.getTime(part);
    }

    public String getAnonymous() {
        return "(anonymous user #" + id + ")";
    }
}
