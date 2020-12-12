package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class Stats {
    @JsonProperty
    HashMap<String, Member> members;
}
