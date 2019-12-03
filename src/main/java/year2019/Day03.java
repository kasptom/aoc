package year2019;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day03 implements IAocTask {

    @Override
    public String getFileName() {
        return "aoc2019/input_03.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        String[] wireA = lines.get(0).split(",");
        String[] wireB = lines.get(1).split(",");
        List<Point> pathA = generatePath(wireA);
        List<Point> pathB = generatePath(wireB);

        Point closest = pathA.stream().filter(p -> p.x != 0 || p.y != 0)
                .filter(p -> pathB.stream().anyMatch(q -> q.compareTo(p) == 0))
                .min((a, b) -> Integer.compare(Math.abs(a.x) + Math.abs(a.y), Math.abs(b.x) + Math.abs(b.y)))
                .orElse(null);

        System.out.println(Math.abs(closest.x) + Math.abs(closest.y));
    }

    private List<Point> generatePath(String[] wire) {
        List<Point> path = new ArrayList<>();
        path.add(new Point(0, 0, 0));

        int totalSteps = 0;
        for (int i = 0; i < wire.length; i++) {
            String direction = wire[i].substring(0, 1);
            Integer steps = Integer.valueOf(wire[i].substring(1));

            int x = 0, y = 0;
            if (direction.equals("D")) {
                x = 0;
                y = -1;
            } else if (direction.equals("U")) {
                x = 0;
                y = 1;
            } else if (direction.equals("L")) {
                x = -1;
                y = 0;
            } else if (direction.equals("R")) {
                x = 1;
                y = 0;
            }

            for (int j = 0; j < steps; j++) {
                totalSteps++;
                Point a = path.get(path.size() - 1);
                path.add(new Point(a.x + x, a.y + y, totalSteps));
            }
        }
        return path;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        String[] wireA = lines.get(0).split(",");
        String[] wireB = lines.get(1).split(",");
        List<Point> pathA = generatePath(wireA);
        List<Point> pathB = generatePath(wireB);

        List<Point> intersections = pathA.stream().filter(p -> p.x != 0 || p.y != 0)
                .filter(p -> pathB.stream().anyMatch(q -> q.compareTo(p) == 0))
                .collect(Collectors.toList());

        List<Point> intersectionsB = pathB.stream().filter(p -> intersections.stream().anyMatch(q -> q.compareTo(p) == 0)).collect(Collectors.toList());

        HashMap<Point, Point> combinedIntersections = new HashMap<>();
        intersections.forEach(point -> {
            combinedIntersections.put(point, intersectionsB.stream().filter(p -> p.compareTo(point) == 0).min(Comparator.comparingInt(p -> p.step)).orElse(null));
        });

        Integer pathLength = intersections.stream()
                .map(p -> p.step + combinedIntersections.get(p).step)
                .min(Integer::compareTo)
                .orElse(null);

        System.out.println(pathLength);
    }

    class Point implements Comparable<Point> {
        private final int step;
        Integer x, y;

        public Point(int i, int i1, int step) {
            x = i;
            y = i1;
            this.step = step;
        }

        @Override
        public int compareTo(Point point) {
            return Math.abs(x.compareTo(point.x)) + Math.abs(y.compareTo(point.y));
        }
    }
}
