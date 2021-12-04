package year2019;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day10Test {

    @Test
    public void pointsAndStation_sort_inClockwiseOrder() {
        List<Day10.Point> asteroids = new ArrayList<>();
        Day10.Point station = new Day10.Point(11, 13);
        System.out.println("station " + station);
        Day10.DistanceComparator comparator = new Day10.DistanceComparator(station);

        asteroids.add(new Day10.Point(11, 12));
        asteroids.add(new Day10.Point(12, 13));
        asteroids.add(new Day10.Point(11, 14));
        asteroids.add(new Day10.Point(10, 13));
        asteroids.add(new Day10.Point(12, 12));
        asteroids.add(new Day10.Point(12, 14));
        asteroids.add(new Day10.Point(10, 14));
        asteroids.add(new Day10.Point(10, 12));
        asteroids.add(new Day10.Point(11, 11));
        asteroids.add(new Day10.Point(13, 13));
        asteroids.add(new Day10.Point(11, 15));
        asteroids.add(new Day10.Point(9, 13));
        asteroids.add(new Day10.Point(12, 11));
        asteroids.add(new Day10.Point(13, 12));
        asteroids.add(new Day10.Point(13, 14));
        asteroids.add(new Day10.Point(12, 15));
        asteroids.add(new Day10.Point(10, 15));
        asteroids.add(new Day10.Point(9, 14));
        asteroids.add(new Day10.Point(9, 12));
        asteroids.add(new Day10.Point(10, 11));
        asteroids.add(new Day10.Point(13, 11));
        asteroids.add(new Day10.Point(13, 15));
        asteroids.add(new Day10.Point(9, 15));
        asteroids.add(new Day10.Point(9, 11));
        Day10 day10 = new Day10();
        day10.station = station;

        List<Day10.Point> copyToSort = day10.getPointsSortedByVaporizationOrder(asteroids, comparator);

        asteroids.forEach(asteroid -> System.out.printf("%s %f%n", asteroid, comparator.getStationClockwiseAngle(asteroid)));
        copyToSort.sort(comparator);

        assertEquals(asteroids, copyToSort);
    }
}
