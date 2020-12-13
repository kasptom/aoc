package stats.model;

import java.util.Comparator;

public class TimestampComparator implements Comparator<Member> {

    private final int dayIdx;
    private final int partIdx;

    public TimestampComparator(int dayIdx, int partIdx) {
        this.dayIdx = dayIdx;
        this.partIdx = partIdx;
    }

    @Override
    public int compare(Member one, Member other) {
        Day oneDay = one.completionDayLevel.get(dayIdx + 1);
        Day otherDay = other.completionDayLevel.get(dayIdx + 1);
        var oneStar = oneDay.getStars().get(partIdx);
        var otherStar = otherDay.getStars().get(partIdx);
        return Long.compare(oneStar.timestamp, otherStar.timestamp);
    }
}
