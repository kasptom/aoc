package stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Stats {
    public static final ZoneOffset AOC_EST_ZONE = ZoneOffset.of("-05:00");
    /**
     * Because of an outage during the day 1 puzzle unlock, day 1 is worth no points.
     * https://adventofcode.com/2020/leaderboard
     */
    private static final int AOC_YEAR_WITH_FIRST_TASK_SHORTAGE = 2020;

    HashMap<Integer, Member> members = new HashMap<>();
    List<List<List<Member>>> ranksPerDayPerPart = new ArrayList<>();
    List<Integer> days;

    int event;

    @JsonProperty(index = 1)
    public void setEvent(int event) {
        this.event = event;
        days = IntStream.range(0, (int) getDaysCount()).boxed().collect(Collectors.toList());
    }

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
        for (int i = 0; i < days.size(); i++) {
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
                .filter(member -> member.getCompletionDayLevel().containsKey(dayIdx + 1))
                .filter(member -> member.getCompletionDayLevel().get(dayIdx + 1).getStars().size() > partIdx)
                .filter(member -> isWithinDeadline(dayIdx, member.getCompletionDayLevel().get(dayIdx + 1), partIdx))
                .collect(Collectors.toList());
    }

    private boolean isWithinDeadline(int dayIdx, Day day, int partIdx) {
        ZonedDateTime dayDeadline = ZonedDateTime.of(event, 12, dayIdx + 2, 0, 0, 0, 0, AOC_EST_ZONE);
        ZonedDateTime submitTime = day.getDateTime(partIdx);
        return submitTime.isBefore(dayDeadline);
    }

    private void updateMemberStats(Member member) {
        member.dayPoints = new ArrayList<>();
        member.daysRanks = new ArrayList<>();
        for (int dayIdx = 0; dayIdx < getDaysCount(); dayIdx++) {
            int pointsFirst = getPoints(dayIdx, member, 0);
            int pointsSecond = getPoints(dayIdx, member, 1);
            int rankFirst = getRank(dayIdx, member, 0);
            int rankSecond = getRank(dayIdx, member, 1);
            member.dayPoints.add(List.of(pointsFirst, pointsSecond));
            member.daysRanks.add(List.of(rankFirst, rankSecond));
        }
        if (event == AOC_YEAR_WITH_FIRST_TASK_SHORTAGE) {
            member.dayPoints.set(0, List.of(0, 0));
            member.daysRanks.set(0, List.of(members.size(), members.size()));
        }
    }

    private int getRank(int dayIdx, Member member, int partIdx) {
        var rank = this.ranksPerDayPerPart.get(dayIdx).get(partIdx).indexOf(member);
        return rank == -1 ? members.size() : rank + 1;
    }

    private int getPoints(int dayIdx, Member member, int partIdx) {
        return members.size() - getRank(dayIdx, member, partIdx) + 1;
    }

    private long getDaysCount() {
        var lastTaskDate = ZonedDateTime.of(event, 12, 25, 0, 0, 0, 0, AOC_EST_ZONE);
        var timeNowEct = ZonedDateTime.now(AOC_EST_ZONE);
        var daysToChristmas = Duration.between(timeNowEct, lastTaskDate).toDays();
        return Math.min(26L, 25L - daysToChristmas);
    }
}
