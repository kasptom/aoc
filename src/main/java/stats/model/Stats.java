package stats.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static stats.factory.StatsFactory.AOC_EST_ZONE;

@Getter
@Setter
public class Stats {
    private Long ownerId;
    private Integer event;
    private HashMap<Long, Member> members;

    private List<List<List<Member>>> ranksPerDayPerPart = new ArrayList<>();
    private List<List<Member>> rankTillDay = new ArrayList<>();
    private List<Integer> days;
    private List<Member> sortedMembers;
    private String ownerName;

    @SuppressWarnings("unused")
    public int getMembersCount() {
        return members.size();
    }

    public int getRank(int dayIdx, Member member, int partIdx) {
        var index = getMemberListIndex(dayIdx, member, partIdx);
        return index == -1 ? members.size() : index + 1;
    }

    public int getPoints(int dayIdx, Member member, int partIdx) {
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
