package stats.factory;

import stats.dto.StatsDto;
import stats.mapper.StatsMapper;
import stats.model.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatsFactory {
    public static final ZoneOffset AOC_EST_ZONE = ZoneOffset.of("-05:00");
    /**
     * Because of an outage during the day 1 puzzle unlock, day 1 is worth no points.
     * https://adventofcode.com/2020/leaderboard
     */
    private static final int AOC_YEAR_WITH_FIRST_TASK_SHORTAGE = 2020;

    public static Stats create(StatsDto statsDto) {
        Stats stats = StatsMapper.INSTANCE.map(statsDto);
        updateMembers(stats);
        updateOwnerName(stats);

        return stats;
    }

    private static void updateOwnerName(Stats stats) {
        Long ownerId = stats.getOwnerId();
        String ownerName = stats.getMembers().get(ownerId).getName();
        stats.setOwnerName(ownerName);
    }

    private static void updateMembers(Stats stats) {
        HashMap<Long, Member> members = stats.getMembers();
        List<Integer> days = createDaysDecreasingOrder(stats);
        stats.setDays(days);

        List<Member> sortedMembers = members.values()
                .stream()
                .sorted((first, second) -> Long.compare(second.getLocalScore(), first.getLocalScore()))
                .collect(Collectors.toList());
        stats.setSortedMembers(sortedMembers);

        updateMemberStats(stats);
    }

    private static List<Integer> createDaysDecreasingOrder(Stats stats) {
        return IntStream.range(0, (int) stats.getDaysCount())
                .boxed().sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private static void updateMemberStats(Stats stats) {
        updateRankPerDays(stats);
        for (var member : stats.getSortedMembers()) {
            updateMemberStats(stats, member);
        }
        updateTillDayRanks(stats);

        System.out.println("init finished");
    }

    private static void updateTillDayRanks(Stats stats) {
        for (var member : stats.getSortedMembers()) {
            for (int dayIdx = 0; dayIdx < stats.getDays().size(); dayIdx++) {
                var rankOrderedMembersAtDay = new ArrayList<>(stats.getSortedMembers());
                rankOrderedMembersAtDay.sort(new PointsTillDayComparator(dayIdx));
                stats.getRankTillDay().add(rankOrderedMembersAtDay);

                member.getTillDayRanks().add(stats.getRankTillDay().get(dayIdx).indexOf(member) + 1);
                if (AOC_YEAR_WITH_FIRST_TASK_SHORTAGE == stats.getEvent() && dayIdx == 0) {
                    member.getTillDayRanks().set(0, stats.getMembers().size());
                }
            }
        }
    }

    private static void updateRankPerDays(Stats stats) {
        for (int i = 0; i < stats.getDays().size(); i++) {
            final int dayIdx = i;
            stats.getSortedMembers().forEach(member -> member
                    .getCompletionDayLevel()
                    .computeIfPresent(dayIdx + 1, (idx, day) -> {
                        if (day.getFirst() != null) day.getStars().add(day.getFirst());
                        if (day.getSecond() != null) day.getStars().add(day.getSecond());
                        return day;
                    }));
            List<Member> firstPart = getCopyWithFilteredUsers(stats, i, 0);
            List<Member> secondPart = getCopyWithFilteredUsers(stats, i, 1);
            firstPart.sort(new TimestampComparator(i, 0));
            secondPart.sort(new TimestampComparator(i, 1));
            stats.getRanksPerDayPerPart().add(List.of(firstPart, secondPart));
        }
    }

    private static List<Member> getCopyWithFilteredUsers(Stats stats, int dayIdx, int partIdx) {
        List<Member> members = new ArrayList<>(stats.getMembers().values());
        return members.stream()
                .filter(member -> member.getCompletionDayLevel().containsKey(dayIdx + 1))
                .filter(member -> member.getCompletionDayLevel().get(dayIdx + 1).getStars().size() > partIdx)
                .filter(member -> isWithinDeadline(stats, dayIdx, member.getCompletionDayLevel().get(dayIdx + 1), partIdx))
                .collect(Collectors.toList());
    }


    private static boolean isWithinDeadline(Stats stats, int dayIdx, Day day, int partIdx) {
        if (stats.getEvent() >= 2015) return true; // it always is
        // ... but I've been working too hard to filter out >24h scores :V
        ZonedDateTime dayDeadline = ZonedDateTime.of(stats.getEvent(), 12, dayIdx + 2, 0, 0, 0, 0, AOC_EST_ZONE);
        ZonedDateTime submitTime = day.getDateTime(partIdx);
        return submitTime.isBefore(dayDeadline);
    }

    private static void updateMemberStats(Stats stats, Member member) {
        member.setDayPoints(new ArrayList<>());
        member.setDaysRanks(new ArrayList<>());
        member.setTillDayRanks(new ArrayList<>());
        for (int dayIdx = 0; dayIdx < stats.getDaysCount(); dayIdx++) {
            int pointsFirst = stats.getPoints(dayIdx, member, 0);
            int pointsSecond = stats.getPoints(dayIdx, member, 1);
            int rankFirst = stats.getRank(dayIdx, member, 0);
            int rankSecond = stats.getRank(dayIdx, member, 1);
            member.getDayPoints().add(List.of(pointsFirst, pointsSecond));
            member.getDaysRanks().add(List.of(rankFirst, rankSecond));

            if (stats.getEvent() == AOC_YEAR_WITH_FIRST_TASK_SHORTAGE) {
                member.getDayPoints().set(0, List.of(0, 0));
                member.getDaysRanks().set(0, List.of(stats.getMembers().size(), stats.getMembers().size()));
            }
        }
    }

    private StatsFactory() {
    }
}
