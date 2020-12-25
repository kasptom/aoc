package stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class StarDto {
    @JsonProperty("get_star_ts")
    long timestamp;
}
