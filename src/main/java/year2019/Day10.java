package year2019;

import aoc.IAocTask;

import java.util.*;

public class Day10 implements IAocTask {

    Point station = null;

    @Override
    public String getFileName() {
        return "aoc2019/input_10.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Point> asteroids = loadAsteroidPositions(lines);
        Map<Point, Set<Point>> asteroidToBaseJump = new TreeMap<>();
        int maxDetected = 0;
        for (Point asteroid : asteroids) {
            HashSet<Point> baseJumps = new HashSet<>();
            asteroidToBaseJump.put(asteroid, baseJumps);

            for (Point otherAsteroid : asteroids) {
                if (otherAsteroid.equals(asteroid)) {
//                    System.out.printf("%s equals %s%n", asteroid, otherAsteroid);
                    continue;
                }
                Point jump = getBaseJumpBetween(asteroid, otherAsteroid);
                baseJumps.add(jump);
            }
//            System.out.println(baseJumps.toString());
            if (maxDetected < baseJumps.size()) {
                maxDetected = baseJumps.size();
                station = asteroid;
            }
        }
        System.out.println(station);
        System.out.println(maxDetected);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Point> asteroids = loadAsteroidPositions(lines);

        DistanceComparator comparator = new DistanceComparator(station);
        List<Point> sortedAsteroids = getPointsSortedByVaporizationOrder(asteroids, comparator);

        final int[] counter = {1};
        sortedAsteroids.forEach(asteroid -> System.out.printf("%d: %s (%.3f) (%.3f) %n",
                counter[0]++, asteroid, comparator.getStationClockwiseAngle(asteroid),
                Point.distanceBetween(asteroid, station)));

        Point twoHundredth = asteroids.get(199);
        System.out.println(twoHundredth.x * 100 + twoHundredth.y);
    }

    List<Point> getPointsSortedByVaporizationOrder(List<Point> asteroids, DistanceComparator comparator) {
        asteroids.remove(station);
        Map<Double, List<Point>> angleToPoint = new TreeMap<>();
        asteroids.forEach(asteroid -> {
            Double angle = comparator.getStationClockwiseAngle(asteroid);
            if (!angleToPoint.containsKey(angle)) {
                angleToPoint.put(angle, new ArrayList<>());
            }
            angleToPoint.get(angle).add(asteroid);
        });
        angleToPoint.keySet().forEach(key -> angleToPoint.get(key).sort(comparator));

        List<Point> sortedAsteroids = new ArrayList<>();
        boolean isEmpty = false;
        while (!isEmpty) {
            isEmpty = true;
            for (Double angle : angleToPoint.keySet()) {
                if (angleToPoint.get(angle).size() > 0) {
                    isEmpty = false;
                    Point vaporized = angleToPoint.get(angle).remove(0);
                    sortedAsteroids.add(vaporized);
                }
            }
        }
        return sortedAsteroids;
    }

    private Point getBaseJumpBetween(Point asteroid, Point otherAsteroid) {
        int x1 = asteroid.x;
        int y1 = asteroid.y;
        int x2 = otherAsteroid.x;
        int y2 = otherAsteroid.y;
        int x = x2 - x1;
        int y = y2 - y1;

        int gcd = getGcd(x, y);
        x /= gcd;
        y /= gcd;
        return new Point(x, y);
    }

    private int getGcd(int x, int y) { // modulo
//        int backX = x;
//        int backY = y;
        x = x < 0 ? -x : x;
        y = y < 0 ? -y : y;

        if (x == 0 || y == 0) return Math.max(x, y);

        while (x != y) {
            //System.out.format("%d %d%n", x, y);
            if (x < y) y = y - x;
            else x = x - y;
        }
//        System.out.printf("GCD %d %d = %d%n", backX, backY, x);
        return x;
    }

    private List<Point> loadAsteroidPositions(List<String> lines) {
        List<Point> asteroids = new ArrayList<>();
        int x;
        int y = 0;
        for (String line : lines) {
            x = 0;
            String[] row = line.split("");
            for (String col : row) {
                if ("#".equals(col)) {
                    asteroids.add(new Point(x, y));
                }
                x++;
            }
            y++;
        }
        return asteroids;
    }

    static class Point implements Comparable<Point> {
        int x, y;
        int color;
        static boolean showColor = false;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            return y == point.y;
        }

        @Override
        public int compareTo(Point other) {
            if (x == other.x && y == other.y) return 0;
            if (x == other.x) {
                return Integer.compare(y, other.y);
            } else {
                return Integer.compare(x, other.x);
            }
        }

        @Override
        public String toString() {
            return !showColor
                    ? String.format("{%3d, %3d}", x, y)
                    : String.format("{%3d, %3d} (%s)", x, y, color == 0 ? "B" : "W");
        }

        static double distanceBetween(Point one, Point another) {
            int x = one.x - another.x;
            int y = one.y - another.y;
            return Math.sqrt(x * x + y * y);
        }
    }

    static class DistanceComparator implements Comparator<Point> {
        private final Point station;

        public DistanceComparator(Point station) {
            this.station = station;
        }

        @Override
        public int compare(Point o1, Point o2) {
            double distance1 = Point.distanceBetween(station, o1);
            double distance2 = Point.distanceBetween(station, o2);
            return Double.compare(distance1, distance2);
        }

        double getStationClockwiseAngle(Point o1) {
            double angle = Math.atan2(station.y - o1.y, station.x - o1.x) * 180.0 / Math.PI;
            angle -= 90;
            angle = angle < 0 ? angle + 360 : angle;
            return angle;
        }

    }
}
