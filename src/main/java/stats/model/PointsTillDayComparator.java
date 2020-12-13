package stats.model;

import java.util.Comparator;

public class PointsTillDayComparator implements Comparator<Member> {
    private final int dayIdx;

    public PointsTillDayComparator(int dayIdx) {
        this.dayIdx = dayIdx;
    }

    @Override
    public int compare(Member memA, Member memB) {
        var onePoints = memA.getPointsAtDay(dayIdx);
        var otherPoints = memB.getPointsAtDay(dayIdx);
        return Integer.compare(otherPoints, onePoints);
    }
}
