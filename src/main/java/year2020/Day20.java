package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day20 implements IAocTask {
    private static final int TILE_SIZE = 10;
    TreeMap<Integer, Tile> tilesMap;
    List<Integer> corners;
    private static final int NORTH = 0;
    private static final int EAST = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 3;
    long firstPartAnswer;

    @Override
    public String getFileName() {
        return "aoc2020/input_20.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += 12) {
            List<String> tileLines = lines.subList(i, i + TILE_SIZE + 1);
            Tile tile = Tile.load(tileLines);
            tiles.add(tile);
        }
        tiles.sort(Comparator.comparingLong(t -> t.id));
//        tiles.forEach(System.out::println);
//        Tile.sideToTileOccurrence.keySet().forEach(key -> System.out.println(key + ": " + Tile.sideToTileOccurrence.get(key)));
//        System.out.println("tiles count: " + tiles.size());
        List<Integer> cornerIds = findCorners(tiles);
        long product = cornerIds.stream().mapToLong(x -> x).reduce(1L, (x, y) -> x * y);
        tilesMap = tiles.stream().collect(Collectors.toMap(tile -> tile.id, Function.identity(), (x, y) -> x, TreeMap::new));
        firstPartAnswer = product;
        System.out.println("product: " + product);
    }

    private List<Integer> findCorners(List<Tile> tiles) {
        TreeMap<Integer, Long> tileIdToLonelySideOccurrences = new TreeMap<>();
        for (var tile : tiles) {
            List<Set<Integer>> lonelySides = Tile.sideToTileOccurrence.values().stream().filter(sideList -> sideList.size() == 1)
                    .collect(Collectors.toList());
            tileIdToLonelySideOccurrences.put(tile.id, lonelySides.stream().filter(singletonList -> singletonList.contains(tile.id)).count());
        }
//        System.out.println("TILE ID TO LONELY SIDE OCCURRENCES");
        tileIdToLonelySideOccurrences.forEach((id, occurrences) -> System.out.println("id: " + id + ", occurs: " + occurrences));
        corners = tileIdToLonelySideOccurrences.keySet().stream().filter(key -> tileIdToLonelySideOccurrences.get(key) == 2).collect(Collectors.toList());
//        System.out.println(corners);
        return corners;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Tile> tiles = new ArrayList<>(tilesMap.values());

        tiles.forEach(tile -> tile.findFittingTiles(tiles));
        tiles.forEach(System.out::println);

        char[][] image = buildImage();

        long minPounds = countPoundsNotBeingPartOfTheMonster(image);
        printImage(DRAGON);
        for (int i = 0; i < 4; i++) {
            rotate90Clockwise(image, image.length);
            printImage(image);
            long poundsCount = countPoundsNotBeingPartOfTheMonster(image);
            if (poundsCount < minPounds) {
                minPounds = poundsCount;
            }
            flipVertical(image, image.length);
            printImage(image);
            poundsCount = countPoundsNotBeingPartOfTheMonster(image);
            if (poundsCount < minPounds) {
                minPounds = poundsCount;
            }
            flipVertical(image, image.length);
            printImage(image);
            poundsCount = countPoundsNotBeingPartOfTheMonster(image);
            if (poundsCount < minPounds) {
                minPounds = poundsCount;
            }
        }

        System.out.println("(1st) Corner product: " + firstPartAnswer);
        System.out.println("(2nd) # not being part of the 🐲: " + minPounds);
    }

    private static final char[][] DRAGON = {
            {'?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '#', '?'},
            {'#', '?', '?', '?', '?', '#', '#', '?', '?', '?', '?', '#', '#', '?', '?', '?', '?', '#', '#', '#'},
            {'?', '#', '?', '?', '#', '?', '?', '#', '?', '?', '#', '?', '?', '#', '?', '?', '#', '?', '?', '?'}};
    int D_WIDTH = 20;
    int D_HEIGHT = 3;

    private long countPoundsNotBeingPartOfTheMonster(char[][] image) {
        HashSet<Point> monstersPounds = new HashSet<>();
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image.length; j++) {
                if (isDragon(i, j, image)) {
                    updatePounds(i, j, monstersPounds);
                }
            }
        }
        int allPounds = 0;
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image.length; j++) {
                if (image[i][j] == '#') {
                    allPounds++;
                }
            }
        }
        var result = allPounds - monstersPounds.size();
        System.out.println(result);
        return result;
    }

    private void updatePounds(int i, int j, HashSet<Point> monstersPounds) {
        for (int k = 0; k < D_HEIGHT; k++) {
            for (int l = 0; l < D_WIDTH; l++) {
                if (DRAGON[k][l] == '#') {
                    monstersPounds.add(new Point(i + k, j + l));
                }
            }
        }
    }

    private boolean isDragon(int i, int j, char[][] image) {
        if (i >= image.length - D_HEIGHT - 1) return false;
        if (j >= image.length - D_WIDTH - 1) return false;
        for (int k = 0; k < D_HEIGHT; k++) {
            for (int l = 0; l < D_WIDTH; l++) {
                if (DRAGON[k][l] == '#' && image[i + k][j + l] != '#') {
                    return false;
                }
            }
        }
        return true;
    }

    private char[][] buildImage() {
        List<Tile> tiles = new ArrayList<>(tilesMap.values());
        int SIDE_SIZE = (int) Math.sqrt(tiles.size());
        System.out.println("building image");
        System.out.println("side size: " + SIDE_SIZE);


        System.out.println("----- TILES ----\n");
        Tile[][] tileGrid = null;
        for (var cornerId : corners) {
            Tile corner = tilesMap.get(cornerId);
            try {
                tileGrid = createTileGrid(SIDE_SIZE, corner);
                validateTileGrid(tileGrid);
                break;
            } catch (Exception e) {
                tileGrid = null;
                System.out.println("Trying other corner because: ");
                e.printStackTrace();
            }
        }
        System.out.println(" -------------- ");
        if (tileGrid == null) throw new RuntimeException("No valid ordering found");
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid.length; j++) {
                System.out.print(tileGrid[i][j].id + " ");
            }
            System.out.println();
        }

        System.out.println("----- EO TILES ----\n");
        System.out.println("--- IMAGE ---\n");
        return toImage(tileGrid);
    }

    private void validateTileGrid(Tile[][] tileGrid) {
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid.length; j++) {
                validateTile(tileGrid, j, i, tileGrid.length);
            }
        }
    }

    private void validateTile(Tile[][] tileGrid, int x, int y, int count) {
//        Tile tile = tileGrid[y][x];
//        if (x == 0 && y == 0 && (tile.getNeigh(NORTH) != null || tile.getNeigh(WEST) != null)) {
//            throw new RuntimeException(String.format("invalid top left (%d, %d) %n%s", x, y, tile));
//        }
//        else if (x == 0 && y == count - 1 && (tile.getNeigh(SOUTH) != null || tile.getNeigh(WEST) != null)) {
//            throw new RuntimeException(String.format("invalid bottom left (%d, %d) %n%s", x, y, tile));
//        }
//        else if (x == 0 && tile.getNeigh(EAST) != null) {
//            throw new RuntimeException(String.format("invalid left edge (%d, %d) %n%s", x, y, tile));
//        }
//        else if (x == count - 1 && tile.getNeigh(WEST) != null) {
//            throw new RuntimeException(String.format("invalid right edge (%d, %d) %n%s", x, y, tile));
//        }
//        else if (y == 0 && tile.getNeigh(NORTH) != null) {
//            throw new RuntimeException(String.format("invalid top edge (%d, %d) %n%s", x, y, tile));
//        }
//        else if (y == count - 1 && tile.getNeigh(SOUTH) != null) {
//            throw new RuntimeException(String.format("invalid bottom dedge (%d, %d) %n%s", x, y, tile));
//        }
//        else if (y > 0) {
//            Tile upper = tileGrid[y - 1][x];
//            if (upper.getNeigh(SOUTH).id != tile.id || tile.getNeigh(NORTH).id != upper.id) {
//                throw new RuntimeException(String.format("invalid (%d, %d) %n%s", x, y, tile));
//            }
//        }
    }

    private char[][] toImage(Tile[][] tileGrid) {
        int IMG_SIDE_NOT_CROPPED = tileGrid.length * TILE_SIZE;
        char[][] image = new char[IMG_SIDE_NOT_CROPPED][IMG_SIDE_NOT_CROPPED];
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid.length; j++) {
                var tile = tileGrid[i][j];
                for (int k = 0; k < TILE_SIZE; k++) {
                    for (int l = 0; l < TILE_SIZE; l++) {
                        image[i * TILE_SIZE + k][j * TILE_SIZE + l] = tile.tile[k][l];
                    }
                }
            }
        }
        printImage(image);
        int IMG_SIDE_CROPPED = tileGrid.length * (TILE_SIZE - 2);
        char[][] cropped = new char[IMG_SIDE_CROPPED][IMG_SIDE_CROPPED];

        cropImage(cropped, image);

        System.out.println("--- CROPPED ---");
        printImage(cropped);
        return cropped;
    }

    private void cropImage(char[][] cropped, char[][] image) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < image.length; i++) {
            if (isToCut(i, image.length)) {
                continue;
            }
            for (int j = 0; j < image.length; j++) {
                if (isToCut(j, image.length)) {
                    continue;
                }
//                System.out.format("x=%d, y=%d%n", x, y);
//                System.out.format("j=%d, i=%d%n", j, i);
                cropped[y][x] = image[i][j];
                x++;
            }
            x = 0;
            y++;
        }
    }

    private boolean isToCut(int notCroppedIdx, int notCroppedLength) {
//        if (notCroppedIdx == 0 || notCroppedIdx == notCroppedLength - 1) {
//            return false;
//        }
        int y = notCroppedIdx % TILE_SIZE;
        return y == 0 || y == 9;
    }

    private void printImage(char[][] image) {
        System.out.println(" ----- image ---");
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                System.out.print(image[i][j]);
                if (image.length % TILE_SIZE == 0 && j % TILE_SIZE == 9) {
                    System.out.print(" ");
                }
            }
            System.out.println();
            if (image.length % TILE_SIZE == 0 && i % TILE_SIZE == 9) {
                System.out.println();
            }
        }
        System.out.println(" ----- ---- ---");
    }

    private Tile[][] createTileGrid(int SIDE_SIZE, Tile corner) {
        Tile[][] imgTiles = new Tile[SIDE_SIZE][SIDE_SIZE];
        while (corner.hasNeigh(WEST) || corner.hasNeigh(NORTH)) {
            corner.rotateClockwise(1);
        }
        imgTiles[0][0] = corner;
//        System.out.print(corner.id + " ");
        Set<Integer> used = new HashSet<>();
        used.add(corner.id);
//        System.out.format("CURR: (%d, %d)%n", 0, 0);
        System.out.println(corner);
        for (int i = 0; i < SIDE_SIZE; i++) {
            for (int j = 0; j < SIDE_SIZE; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                Tile prev = j == 0 ? imgTiles[i - 1][0] : imgTiles[i][j - 1];
                Tile current = j == 0 ? prev.getNeigh(SOUTH) : prev.getNeigh(EAST);

                System.out.println("PREV:");
                System.out.println(prev);
                System.out.format("CURR: (%d, %d)%n", i, j);
                System.out.println(current);

                if (used.contains(current.id)) {
                    throw new RuntimeException("tile already used " + current.id);
                }
                imgTiles[i][j] = current;
//                System.out.print(current.id + " ");
                int dir = j == 0 ? SOUTH : EAST;
                while (current.getNeighDir(prev) != dir) {
//                    System.out.println("------ ROTATION OF CURRENT ----- ");
                    current.rotateClockwise(1);
//                    System.out.println(current);
                }
                if (current.isFlippedVertically(prev, dir)) {
                    current.flipVertically();
                }
            }
//            System.out.println(" ------ NEXT ROW ------ ");
//            System.out.println();
        }
        return imgTiles;
    }

    static class Tile {
        static final TreeMap<Long, Set<Integer>> sideToTileOccurrence = new TreeMap<>();
        int id;
        long[] sides; // N E S W (clockwise)
        String[] sidesDebug;
        int northOffset;
        char[][] tile;
        Map<Long, Tile> sideToFittingTile = new HashMap<>();

        public Tile(int id, char[][] tile, long[] sides, String[] sidesDebug, int northOffset) {
            this.id = id;
            this.tile = tile;
            this.sides = sides;
            this.sidesDebug = sidesDebug;
            this.northOffset = northOffset;
        }

        public static Tile load(List<String> tileLines) {
            int id = Integer.parseInt(tileLines.get(0).replace("Tile ", "").replace(":", ""));
            char[][] tile = loadTile(tileLines.subList(1, 1 + TILE_SIZE));
            String[] sidesDebug = getSides(tile);
            long[] sides = loadSidesForTile(sidesDebug, id);
            return new Tile(id, tile, sides, sidesDebug, 0);
        }

        private static String[] getSides(char[][] tile) {
            String[] sides = {"", "", "", ""};
            for (int i = 0; i < TILE_SIZE; i++) { // read the sides clockwise (imagine running around it)
                sides[0] += tile[0][i];
                sides[1] += tile[i][TILE_SIZE - 1];
                sides[2] += tile[TILE_SIZE - 1][TILE_SIZE - 1 - i];
                sides[3] += tile[TILE_SIZE - 1 - i][0];
            }
            return sides;
        }

        private static long[] loadSidesForTile(String[] sidesDebug, int id) {
            long[] sides = new long[4];
            for (int i = 0; i < 4; i++) {
                sides[i] = getSideCode(sidesDebug[i]);
                sideToTileOccurrence.putIfAbsent(Math.abs(sides[i]), new TreeSet<>());
                sideToTileOccurrence.get(Math.abs(sides[i])).add(id);
            }
            return sides;
        }

        /**
         * #..##..###  --->  ###..##..#  #..##..### --> 11100110011001100111
         * ###..##..#  -->   ###..##..#  #..##..###  --> 11100110011001100111
         */
        private static long getSideCode(String side) {
            String codeClockwise = side.replaceAll("\\.", "0").replaceAll("#", "1");
            String codeCounterClockwise = new StringBuilder(codeClockwise).reverse().toString();
            String compressed = codeClockwise.compareTo(codeCounterClockwise) > 0 ? codeClockwise : codeCounterClockwise;
            return Long.parseLong(compressed, 2);
        }

        private static char[][] loadTile(List<String> subList) {
            char[][] tile = new char[TILE_SIZE][TILE_SIZE];
            for (int i = 0; i < TILE_SIZE; i++) {
                for (int j = 0; j < TILE_SIZE; j++) {
                    tile[i][j] = subList.get(i).charAt(j);
                }
            }
            return tile;
        }

        void setSide(int dir, long value, String valueStr) {
            sides[(northOffset + dir) % 4] = value;
            sidesDebug[(northOffset + dir) % 4] = valueStr;
        }

        long getSide(int dir) {
            return sides[(northOffset + dir) % 4];
        }

        String getSideDebug(int idx) {
            return sidesDebug[(northOffset + idx) % 4];
        }

        void findFittingTiles(List<Tile> tiles) {
            sideToFittingTile = new HashMap<>();
            for (Long sideCode : sides) {
                Tile fitting = findFittingTile(tiles, sideCode);
                sideToFittingTile.put(sideCode, fitting);
            }
        }

        private Tile findFittingTile(List<Tile> tiles, Long sideCode) {
            var fittingTiles = tiles.stream()
                    .filter(tile -> tile.id != id)
                    .filter(tile -> countFittingSides(tile, sideCode) == 1)
                    .collect(Collectors.toList());
            if (fittingTiles.size() > 1) throw new RuntimeException("too many fitting tiles :(");
            if (fittingTiles.size() == 0) return null;
            return fittingTiles.get(0);
        }

        private int countFittingSides(Tile tile, Long sideCode) {
            int fittingCount = 0;
            for (int i = 0; i < 4; i++) {
                if (tile.getSide(i) == sideCode) {
                    fittingCount++;
                }
            }
            if (fittingCount > 1) throw new RuntimeException("Too much fitting sides :OOO ");
            return fittingCount;
        }

        private static final String[] NESW = {"N", "E", "S", "W"};

        Tile getNeigh(int dir) {
            return sideToFittingTile.get(getSide(dir));
        }

        @Override
        public String toString() {
            StringBuilder tileBuilder = new StringBuilder();
//            tileBuilder.append("Tile: ").append(id).append("\n");
            for (int i = 0; i < TILE_SIZE; i++) {
                for (int j = 0; j < TILE_SIZE; j++) {
                    tileBuilder.append(tile[i][j]);
                }
                if (i == 0) {
                    tileBuilder.append("\t").append("Tile: ").append(id);
                }

                if (i < 6 && i >= 2) {
                    int idx = i - 2;
                    tileBuilder.append("\t").append(NESW[idx]).append(": ")
                            .append(getSideDebug(idx)).append(" (").append(getSide(idx)).append(")");
                }

                if (i == 8) {
                    tileBuilder.append("\t neighbours: ");
                    for (int dirIdx = 0; dirIdx < 4; dirIdx++) {
                        var neigh = getNeigh(dirIdx);
                        tileBuilder.append(NESW[dirIdx]).append(": ").append(neigh != null ? neigh.id : "none").append(", ");
                    }
                }

                tileBuilder.append("\n");
            }
//            for (int idx = 0; idx < 4; idx++) {
//                tileBuilder.append("\t").append(NESW[idx]).append(": ").append(getSideDebug(idx)).append(" (").append(getSide(idx)).append(")");
//            }

            return tileBuilder.toString();
        }

        public boolean hasNeigh(int coord) {
            return sideToFittingTile.get(getSide(coord)) != null;
        }

        public void rotateClockwise(int times) {
            northOffset = 4 + northOffset - (times % 4);
            northOffset = northOffset % 4;
            if (times % 2 == 0) {
                for (int i = 0; i < 4; i++) {
                    sidesDebug[i] = new StringBuilder(sidesDebug[i]).reverse().toString();
                }
            }
            rotateTile(times);
        }

        public void flipVertically() {
            flipVertical(tile, tile.length);
            long tmp = getSide(NORTH);
            String tmpStr = getSideDebug(NORTH);
            setSide(NORTH, getSide(SOUTH), getSideDebug(SOUTH));
            setSide(SOUTH, tmp, tmpStr);
        }

        private void rotateTile(int times) {
            for (int i = 0; i < times; i++) {
                rotate90Clockwise(tile, TILE_SIZE);
            }
        }

        public int getNeighDir(Tile neigh) {
            long sideWithNeigh = getMatchingSideCode(neigh);
            int sideDir = getSideDir(sideWithNeigh);
            return getOpposite(sideDir);
        }

        private int getOpposite(int dir) {
            if (dir < 0 || dir > 3) throw new RuntimeException("Dir not in range (N, S, W, E)");
            switch (dir) {
                case NORTH:
                    return SOUTH;
                case EAST:
                    return WEST;
                case SOUTH:
                    return NORTH;
                case WEST:
                    return EAST;
                default:
                    throw new RuntimeException("wrong dir code " + dir);
            }
        }

        private int getSideDir(long sideCode) {
            for (int i = 0; i < 4; i++) {
                if (getSide(i) == sideCode) {
                    return i;
                }
            }
            throw new RuntimeException("Side code not found " + sideCode);
        }

        private long getMatchingSideCode(Tile neigh) {
            long matchingSideCode;
            for (var sideCode : sides) {
                Tile fitting = sideToFittingTile.get(sideCode);
                if (fitting != null && fitting.id == neigh.id) {
                    matchingSideCode = sideCode;
                    return matchingSideCode;
                }
            }
            throw new RuntimeException("no side with neigh found (neigh id = " + neigh.id + ")");
        }

        public boolean isFlippedVertically(Tile prev, int neighDirection) {
            if (neighDirection == EAST) {
                for (int i = 0; i < TILE_SIZE; i++) {
                    if (prev.tile[i][TILE_SIZE - 1] != tile[i][0]) {
                        return true;
                    }
                }
            } else if (neighDirection == SOUTH) {
                for (int i = 0; i < TILE_SIZE; i++) {
                    if (prev.tile[TILE_SIZE - 1][i] != tile[0][i]) {
                        return true;
                    }
                }
            } else throw new RuntimeException("Not supported dir " + neighDirection);
            return false;
        }

        enum Dir {NORTH, EAST, SOUTH, WEST}
    }

    // https://www.geeksforgeeks.org/rotate-a-matrix-by-90-degree-in-clockwise-direction-without-using-any-extra-space/
    static void rotate90Clockwise(char[][] matrix, int N) {
        // Traverse each cycle
        for (int i = 0; i < N / 2; i++) {
            for (int j = i; j < N - i - 1; j++) {
                char temp = matrix[i][j];
                matrix[i][j] = matrix[N - 1 - j][i];
                matrix[N - 1 - j][i] = matrix[N - 1 - i][N - 1 - j];
                matrix[N - 1 - i][N - 1 - j] = matrix[j][N - 1 - i];
                matrix[j][N - 1 - i] = temp;
            }
        }
    }
    static void flipVertical(char[][] matrix, int N) {
        for (int i = 0; i < N / 2; i++) {
            for (int j = 0; j < N; j++) {
                char tmp = matrix[i][j];
                matrix[i][j] = matrix[N - 1 - i][j];
                matrix[N - 1 - i][j] = tmp;
            }
        }
    }

    static class Point {
        int x; int y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
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
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
}
