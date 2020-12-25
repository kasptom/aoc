package stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class MemberDto {
    @JsonProperty
    private long id;

    @JsonProperty
    private int stars;

    @JsonProperty
    private String name;

    @JsonProperty("local_score")
    private int localScore;

    @JsonProperty("global_score")
    private long globalScore;

    @JsonProperty("last_star_ts")
    private long lastStartTimestamp;

    @JsonProperty("completion_day_level")
    private HashMap<Integer, DayDto> completionDayLevel;
}
