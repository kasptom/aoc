package stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class StatsDto {
    @JsonProperty("owner_id")
    Long ownerId;

    @JsonProperty
    Integer event;

    @JsonProperty
    HashMap<Long, MemberDto> members;
}
