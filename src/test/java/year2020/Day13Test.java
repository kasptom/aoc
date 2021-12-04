package year2020;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day13Test {

    @Test
    public void getNearestTimestampWithConsecutiveArrivals1() {
        List<Integer> buses = List.of(17, 0, 13, 19);
        var day = new Day13();

        long earliestTimestamp = day.getNearestTimestampWithConsecutiveArrivals(buses);

        print(earliestTimestamp);
        assertEquals(3417, earliestTimestamp);
    }


    @Test
    public void getNearestTimestampWithConsecutiveArrivals2() {
        List<Integer> buses = List.of(67, 7, 59, 61);
        var day = new Day13();

        long earliestTimestamp = day.getNearestTimestampWithConsecutiveArrivals(buses);

        print(earliestTimestamp);
        assertEquals(754018, earliestTimestamp);
    }

    @Test
    public void getNearestTimestampWithConsecutiveArrivals3() {
        List<Integer> buses = List.of(67, 0, 7, 59, 61);
        var day = new Day13();

        long earliestTimestamp = day.getNearestTimestampWithConsecutiveArrivals(buses);

        print(earliestTimestamp);
        assertEquals(779210, earliestTimestamp);
    }

    @Test
    public void getNearestTimestampWithConsecutiveArrivals4() {
        List<Integer> buses = List.of(67, 7, 0, 59, 61);
        var day = new Day13();

        long earliestTimestamp = day.getNearestTimestampWithConsecutiveArrivals(buses);

        print(earliestTimestamp);
        assertEquals(1261476, earliestTimestamp);
    }

    @Test
    public void getNearestTimestampWithConsecutiveArrivals5() {
        List<Integer> buses = List.of(1789, 37, 47, 1889);
        var day = new Day13();

        long earliestTimestamp = day.getNearestTimestampWithConsecutiveArrivals(buses);

        print(earliestTimestamp);
        assertEquals(1202161486, earliestTimestamp);
    }

    private void print(long earliestTimestamp) {
        System.out.println("x = " + earliestTimestamp);
        System.out.println("----------\n");
    }
}
