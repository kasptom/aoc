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
        System.out.println("init finished");
    }


    private void updateRankPerDays() {
        for (int i = 0; i < 25; i++) {
            final int dayIdx = i;
            sortedMembers.forEach(member -> member
                    .getCompletionDayLevel()
                    .computeIfPresent(dayIdx + 1, (idx, day) -> {
                        if (day.first != null) day.stars.add(day.first);
                        if (day.second != null) day.stars.add(day.second);
                        return day;
                    }));
            List<Member> firstPart = getCopyWithFilteredUsers(i, 0);
            List<Member> secondPart = getCopyWithFilteredUsers(i, 1);
            firstPart.sort(new TimestampComparator(i, 0));
            secondPart.sort(new TimestampComparator(i, 1));
            ranksPerDayPerPart.add(List.of(firstPart, secondPart));
        }
    }

    private List<Member> getCopyWithFilteredUsers(int dayIdx, int partIdx) {
        List<Member> members = new ArrayList<>(this.members.values());
        return members.stream()
                .filter(member -> member.getCompletionDayLevel().containsKey(dayIdx + 1)
                        && member.getCompletionDayLevel().get(dayIdx + 1).getStars().size() > partIdx)
                .collect(Collectors.toList());
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
        var rank = this.ranksPerDayPerPart.get(dayIdx).get(partIdx).indexOf(member);
        return rank == -1 ? members.size() : rank + 1;
    }

    private int getPoints(int dayIdx, Member member, int partIdx) {
        return members.size() - getRank(dayIdx, member, partIdx) + 1;
    }
}
