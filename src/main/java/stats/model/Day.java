package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Day {
    @JsonProperty("1")
    Star first;

    @JsonProperty("2")
    Star second;
}
