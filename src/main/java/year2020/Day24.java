package year2020;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 implements IAocTask {

    static HashMap<Point, TileNode> pointToTile = new HashMap<>();

    @Override
    public String getFileName() {
        return "aoc2020/input_24.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<List<TileCode>> tileLocations = lines.stream().map(TileCode::parse).collect(Collectors.toList());
//        System.out.println(tileLocations);
        TileNode reference = new TileNode(new Point(0, 0, 0));
        pointToTile.put(reference.point, reference);
        for (var locations : tileLocations) {
            reference.move(locations, 0);
        }
        long blackCount = pointToTile.values().stream().filter(tile -> tile.color == TileNode.Color.BLACK).count();
        System.out.println(blackCount);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int days = 100;
        for (int i = 0; i < days; i++) {
            flipSimultaneously();
        }
        long blackCount = pointToTile.values().stream().filter(tile -> tile.color == TileNode.Color.BLACK).count();
        System.out.println(blackCount);
    }

    private void flipSimultaneously() {
        surroundBlackTilesWithWhite();
        HashMap<Point, TileNode> copy = copy(pointToTile);
        for (var point : copy.keySet()) {
            if (pointToTile.get(point).shouldFlip()) {
                copy.get(point).flipColor();
            }
        }
        pointToTile = copy;
    }

    private HashMap<Point, TileNode> copy(HashMap<Point, TileNode> pointToTile) {
        HashMap<Point, TileNode> copy = new HashMap<>();
        pointToTile.forEach((k, v) -> copy.put(k, v.copy()));
        return copy;
    }

    private void surroundBlackTilesWithWhite() {
        var blackTiles = pointToTile.values().stream().filter(tile -> tile.color == TileNode.Color.BLACK).collect(Collectors.toList());
        for (var tile : blackTiles) {
            for (var dir : TileCode.values()) {
                Point diff = TileCode.To_MOVE.get(dir);
                Point neigh = tile.point.add(diff);
                if (!pointToTile.containsKey(neigh)) {
                    pointToTile.put(neigh, new TileNode(neigh));
                }
            }
        }
    }

    static class TileNode {
        final Point point;
        Color color;

        public TileNode(Point point) {
            this.point = point;
            color = Color.WHITE;
        }

        public void move(List<TileCode> locations, int idx) {
            if (idx == locations.size()) {
//                System.out.println("flipping color at " + point + " from " + color + " to " + Color.TO_OPPOSITE.get(color));
                flipColor();
                return;
            }
            TileCode code = locations.get(idx);
            Point diff = TileCode.To_MOVE.get(code);
            Point nextPoint = point.add(diff);
            TileNode next = pointToTile.get(nextPoint);
            if (next == null) {
                next = new TileNode(nextPoint);
                pointToTile.put(nextPoint, next);
            }
            next.move(locations, idx + 1);
        }

        /**
         * - Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
         * - Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
         */
        public boolean shouldFlip() {
            int blackNeighsCount = 0;
            for (var dir : TileCode.values()) {
                var neigh = point.add(TileCode.To_MOVE.get(dir));
                if (!pointToTile.containsKey(neigh)) {
                    continue;
                }
                var neighTile = pointToTile.get(neigh);
                if (neighTile.color == Color.BLACK) {
                    blackNeighsCount++;
                }
            }
            return color == Color.BLACK && (blackNeighsCount > 2 || blackNeighsCount == 0)
                    || color == Color.WHITE && blackNeighsCount == 2;
        }

        enum Color {
            WHITE, BLACK;
            public static final Map<Color, Color> TO_OPPOSITE = Map.of(WHITE, BLACK, BLACK, WHITE);
        }

        private void flipColor() {
            color = Color.TO_OPPOSITE.get(color);
        }

        TileNode copy() {
            TileNode copy = new TileNode(this.point);
            copy.color = color;
            return copy;
        }

        @Override
        public String toString() {
            return point.toString() + ", " + color.toString();
        }
    }

    static class Point {
        int x;
        int y;
        int z;

        public Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Point add(Point other) {
            return new Point(x + other.x, y + other.y, z + other.z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            if (y != point.y) return false;
            return z == point.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d, %d)", x, y, z);
        }
    }
}

enum TileCode {
    e, se, sw, w, nw, ne;
    private static final String REGEX = "(e|se|sw|w|nw|ne)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    public static final Map<TileCode, Day24.Point> To_MOVE = Map.of(
            // https://www.redblobgames.com/grids/hexagons/#neighbors
            e, new Day24.Point(1, -1, 0),
            se, new Day24.Point(0, -1, 1),
            sw, new Day24.Point(-1, 0, 1),
            w, new Day24.Point(-1, 1, 0),
            nw, new Day24.Point(0, 1, -1),
            ne, new Day24.Point(1, 0, -1)
    );

    public static List<TileCode> parse(String line) {
        List<TileCode> tileLocation = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(line);
        while (matcher.find()) {
            tileLocation.add(TileCode.valueOf(matcher.group(0)));
        }
        return tileLocation;
    }
}
