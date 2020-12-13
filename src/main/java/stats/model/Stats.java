package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Stats {
    @JsonProperty
    HashMap<String, Member> members;

    @JsonProperty
    String event;

    public List<Member> getMembersSorted() {
        return members.values()
                .stream()
                .sorted((first, second) -> Long.compare(second.localScore, first.localScore))
                .collect(Collectors.toList());
    }

    public int getMembersCount() {
        return members.size();
    }
}
