package stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class StatsDto {
    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty
    private Integer event;

    @JsonProperty
    private HashMap<Long, MemberDto> members;
}
