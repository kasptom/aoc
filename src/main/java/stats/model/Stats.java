package stats.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class Stats {
    public static final ZoneOffset AOC_EST_ZONE = ZoneOffset.of("-05:00");
    /**
     * Because of an outage during the day 1 puzzle unlock, day 1 is worth no points.
     * https://adventofcode.com/2020/leaderboard
     */
    private static final int AOC_YEAR_WITH_FIRST_TASK_SHORTAGE = 2020;

    private Long ownerId;
    private Integer event;
    private HashMap<Long, Member> members;

    private List<List<List<Member>>> ranksPerDayPerPart = new ArrayList<>();
    private List<List<Member>> rankTillDay = new ArrayList<>();
    private List<Integer> days;
    private List<Member> sortedMembers;
    private String ownerName;

    public void updateOwnerName(Long ownerId) {
        ownerName = members.get(ownerId).getName();
    }

    public void updateMembers(HashMap<Long, Member> members) {
        days = IntStream.range(0, (int) getDaysCount()).boxed().collect(Collectors.toList());
        sortedMembers = members.values()
                .stream()
                .sorted((first, second) -> Long.compare(second.getLocalScore(), first.getLocalScore()))
                .collect(Collectors.toList());
        updateMemberStats();
    }

    public int getMembersCount() {
        return members.size();
    }

    private void updateMemberStats() {
        updateRankPerDays();
        for (var member : sortedMembers) {
            updateMemberStats(member);
        }
        updateTillDayRanks();

        System.out.println("init finished");
    }

    private void updateTillDayRanks() {
        for (var member : sortedMembers) {
            for (int dayIdx = 0; dayIdx < days.size(); dayIdx++) {
                var rankOrderedMembersAtDay = new ArrayList<>(sortedMembers);
                rankOrderedMembersAtDay.sort(new PointsTillDayComparator(dayIdx));
                rankTillDay.add(rankOrderedMembersAtDay);

                member.getTillDayRanks().add(rankTillDay.get(dayIdx).indexOf(member) + 1);
                if (AOC_YEAR_WITH_FIRST_TASK_SHORTAGE == event && dayIdx == 0) {
                    member.getTillDayRanks().set(0, members.size());
                }
            }
        }
    }

    private void updateRankPerDays() {
        for (int i = 0; i < days.size(); i++) {
            final int dayIdx = i;
            sortedMembers.forEach(member -> member
                    .getCompletionDayLevel()
                    .computeIfPresent(dayIdx + 1, (idx, day) -> {
                        if (day.getFirst() != null) day.getStars().add(day.getFirst());
                        if (day.getSecond() != null) day.getStars().add(day.getSecond());
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
        return true;
//        ZonedDateTime dayDeadline = ZonedDateTime.of(event, 12, dayIdx + 2, 0, 0, 0, 0, AOC_EST_ZONE);
//        ZonedDateTime submitTime = day.getDateTime(partIdx);
//        return submitTime.isBefore(dayDeadline);
    }

    private void updateMemberStats(Member member) {
        member.setDayPoints(new ArrayList<>());
        member.setDaysRanks(new ArrayList<>());
        member.setTillDayRanks(new ArrayList<>());
        for (int dayIdx = 0; dayIdx < getDaysCount(); dayIdx++) {
            int pointsFirst = getPoints(dayIdx, member, 0);
            int pointsSecond = getPoints(dayIdx, member, 1);
            int rankFirst = getRank(dayIdx, member, 0);
            int rankSecond = getRank(dayIdx, member, 1);
            member.getDayPoints().add(List.of(pointsFirst, pointsSecond));
            member.getDaysRanks().add(List.of(rankFirst, rankSecond));

            if (event == AOC_YEAR_WITH_FIRST_TASK_SHORTAGE) {
                member.getDayPoints().set(0, List.of(0, 0));
                member.getDaysRanks().set(0, List.of(members.size(), members.size()));
            }
        }
    }

    private int getRank(int dayIdx, Member member, int partIdx) {
        var index = getMemberListIndex(dayIdx, member, partIdx);
        return index == -1 ? members.size() : index + 1;
    }

    private int getPoints(int dayIdx, Member member, int partIdx) {
        var index = getMemberListIndex(dayIdx, member, partIdx);
        if (index == -1) return 0;
        return members.size() - getRank(dayIdx, member, partIdx) + 1;
    }

    private int getMemberListIndex(int dayIdx, Member member, int partIdx) {
        return ranksPerDayPerPart.get(dayIdx).get(partIdx).indexOf(member);
    }

    public long getDaysCount() {
        var lastTaskDateStart = ZonedDateTime.of(event, 12, 25, 0, 0, 0, 0, AOC_EST_ZONE);
        var timeNowEct = ZonedDateTime.now(AOC_EST_ZONE);
//        var timeNowEct = ZonedDateTime.of(event, 12, 13, 23, 59, 0, 0, AOC_EST_ZONE);
        var daysToChristmas = Math.ceil(Duration.between(timeNowEct, lastTaskDateStart).toSeconds() / 86400.0);
        return Math.min(25L, 25L - (long) daysToChristmas);
    }
}