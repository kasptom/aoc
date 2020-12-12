package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class Member {
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
}
