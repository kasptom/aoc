package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Star {
    @JsonProperty("get_star_ts")
    long timestamp;
}
