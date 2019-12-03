package year2019;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        path.add(new Point(0, 0));

        for (int i = 0; i < wire.length; i++) {
            String direction = wire[i].substring(0,1);
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
                Point a = path.get(path.size() - 1);
                path.add(new Point(a.x + x, a.y + y));
            }
        }
        return path;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
    }

    class Point implements Comparable<Point>{
        Integer x, y;

        public Point(int i, int i1) {
            x = i;
            y = i1;
        }

        @Override
        public int compareTo(Point point) {
            return Math.abs(x.compareTo(point.x)) + Math.abs(y.compareTo(point.y));
        }
    }
}
