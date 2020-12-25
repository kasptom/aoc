package stats.model;

import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@RequiredArgsConstructor
public class TimestampComparator implements Comparator<Member> {

    private final int dayIdx;
    private final int partIdx;

    @Override
    public int compare(Member one, Member other) {
        Day oneDay = one.getCompletionDayLevel().get(dayIdx + 1);
        Day otherDay = other.getCompletionDayLevel().get(dayIdx + 1);
        var oneStar = oneDay.getStars().get(partIdx);
        var otherStar = otherDay.getStars().get(partIdx);
        return Long.compare(oneStar.getTimestamp(), otherStar.getTimestamp());
    }
}
