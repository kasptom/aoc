package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Stats {
    HashMap<String, Member> members = new HashMap<>();

    @JsonProperty
    String event;

    private List<Member> sortedMembers;

    @JsonProperty
    public void setMembers(HashMap<String, Member> members) {
        this.members = members;
        this.sortedMembers = members.values()
                .stream()
                .sorted((first, second) -> Long.compare(second.localScore, first.localScore))
                .collect(Collectors.toList());
        this.updateMemberStats();
    }

    public List<Member> getMembersSorted() {
        return sortedMembers;
    }

    public int getMembersCount() {
        return members.size();
    }

    private void updateMemberStats() {
        for (var member: sortedMembers) {
            updateMemberStats(member);
        }
    }

    private void updateMemberStats(Member member) {
        member.dayPoints = new ArrayList<>();
        member.daysRanks = new ArrayList<>();
        for (int dayIdx = 0; dayIdx < 25; dayIdx++) {
            member.dayPoints.add(List.of(0, 0));
            member.daysRanks.add(List.of(0, 0));
        }
    }
}
