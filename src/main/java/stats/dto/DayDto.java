package stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DayDto {
    @JsonProperty("1")
    private StarDto first;

    @JsonProperty("2")
    private StarDto second;
}
