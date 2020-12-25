package stats.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Member {
    static final String MEMBER_ROW_FORMAT = "%-25s %12d %12d";

    long id;
    int stars;
    String name;
    int localScore;
    long globalScore;
    long lastStartTimestamp;

    List<Integer> scoreHistory = new ArrayList<>();
    HashMap<Integer, Day> completionDayLevel;
    List<List<Integer>> daysRanks;
    List<List<Integer>> dayPoints;
    List<Integer> tillDayRanks;

    public int getStarsForDay(int dayIdx) {
        if (!completionDayLevel.containsKey(dayIdx + 1)) {
            return 0;
        }
        Day day = completionDayLevel.get(dayIdx + 1);
        return day.getStarsCount();
    }

    public String getTimestamp(int dayIdx, int part) {
        var day = completionDayLevel.get(dayIdx + 1);
        return day == null ? "N/A" : day.getTimestampStr(part);
    }

    public String getAnonymous() {
        return "(anonymous user #" + id + ")";
    }

    public int getDayRank(int dayIdx, int partIdx) {
        return daysRanks.get(dayIdx).get(partIdx);
    }

    public int getDayPoints(int dayIdx, int partIdx) {
        return dayPoints.get(dayIdx).get(partIdx);
    }

    public int getPointsAtDay(int dayIdx) {
        return dayPoints.subList(0, dayIdx + 1)
                .stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getRankAtDay(int dayIdx) {
        return tillDayRanks.get(dayIdx);
    }

    public int getPointsDelta(int dayIdx) {
        return getPointsAtDay(dayIdx) - getPointsAtDay(dayIdx - 1);
    }

    public int getRankDelta(int dayIdx) {
        if (dayIdx == 0) return 0;
        return getRankAtDay(dayIdx - 1) - getRankAtDay(dayIdx);
    }
}
