package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Stats {
    HashMap<Integer, Member> members = new HashMap<>();
    List<List<List<Member>>> ranksPerDayPerPart = new ArrayList<>();

    @JsonProperty
    String event;

    private List<Member> sortedMembers;

    @JsonProperty
    public void setMembers(HashMap<Integer, Member> members) {
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
        this.updateRankPerDays();
        for (var member : sortedMembers) {
            updateMemberStats(member);
        }
    }

    private void updateRankPerDays() {
        List<Member> firstPart = new ArrayList<>(members.values());
        List<Member> secondPart = new ArrayList<>(members.values());

        // TODO sort by timestamps

        for (int i = 0; i < 25; i++) {
            ranksPerDayPerPart.add(List.of(firstPart, secondPart));
        }
    }

    private void updateMemberStats(Member member) {
        member.dayPoints = new ArrayList<>();
        member.daysRanks = new ArrayList<>();
        for (int dayIdx = 0; dayIdx < 25; dayIdx++) {
            int pointsFirst = getPoints(dayIdx, member, 0);
            int pointsSecond = getPoints(dayIdx, member, 1);
            int rankFirst = getRank(dayIdx, member, 0);
            int rankSecond = getRank(dayIdx, member, 1);
            member.dayPoints.add(List.of(pointsFirst, pointsSecond));
            member.daysRanks.add(List.of(rankFirst, rankSecond));
        }
    }

    private int getRank(int dayIdx, Member member, int partIdx) {
        return this.ranksPerDayPerPart.get(dayIdx).get(partIdx).indexOf(member);
    }

    private int getPoints(int dayIdx, Member member, int partIdx) {
        return 100 - getRank(dayIdx, member, partIdx);
    }
}
