package year2020;

import aoc.IAocTask;

import java.util.List;
import java.util.function.BiFunction;

public class Day11 implements IAocTask {
    char[][] backup;
    char[][] layout;
    private static final char CHAIR = 'L';
    private static final char PERSON = '#';
    private static final char FLOOR = '.';
    private static final int MAX_NEIGH = 8;
    private int width;
    private int height;
    private final int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
    private final int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

    @Override
    public String getFileName() {
        return "aoc2020/input_11.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadLayout(lines);
//        printLayout();
        BiFunction<Integer, Integer, Integer> neighbourCounter = this::countOccupiedAdjacent;
        while (updateLayout(neighbourCounter, 4)) {
        }
        ;
        int occupied = getOccupiedAfterLastChange();
        System.out.println(occupied);
//        printLayout();
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        loadLayout(lines);
//        printLayout();
        BiFunction<Integer, Integer, Integer> neighbourCounter = this::countOccupiedAdjacentV2;
        while (updateLayout(neighbourCounter, 5)) {
        }
        ;
        int occupied = getOccupiedAfterLastChange();
        System.out.println(occupied);
//        printLayout();
    }

    private int countOccupiedAdjacentV2(int x, int y) {
        int counter = 0;
        for (int i = 0; i < MAX_NEIGH; i++) {
            int dirX = dx[i];
            int dirY = dy[i];
            if (findInDirection(x, y, dirX, dirY)) {
                counter++;
            }
        }
        return counter;
    }

    private boolean findInDirection(int x, int y, int dirX, int dirY) {
        int neighX = x + dirX;
        int neighY = y + dirY;
        if (!isInRange(neighX, width - 1) || !isInRange(neighY, height - 1)) {
            return false;
        }
        char state = layout[neighY][neighX];
        if (state == PERSON) {
            return true;
        }
        if (state == CHAIR) {
            return false;
        }
        if (state == FLOOR) {
            return findInDirection(neighX, neighY, dirX, dirY);
        }
        return false;
    }

    private int getOccupiedAfterLastChange() {
        int count = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (layout[i][j] == PERSON) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean updateLayout(BiFunction<Integer, Integer, Integer> neighbourCounter, int threshold) {
        boolean updated = false;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isEmptyAndNoNeighbours(x, y, neighbourCounter)) {
                    backup[y][x] = PERSON;
                    updated = true;
                } else if (isOccupiedAndAtLeast(x, y, neighbourCounter, threshold)) {
                    backup[y][x] = CHAIR;
                    updated = true;
                } else {
                    backup[y][x] = layout[y][x];
                }
            }
        }
        if (!updated) {
            return false;
        }
        for (int i = 0; i < height; i++) {
            System.arraycopy(backup[i], 0, layout[i], 0, width);
        }
        return true;
    }

    private boolean isOccupiedAndAtLeast(int x, int y, BiFunction<Integer, Integer, Integer> neighbourCounter, int threshold) {
        return layout[y][x] == PERSON && neighbourCounter.apply(x, y) >= threshold;
    }

    private boolean isEmptyAndNoNeighbours(int x, int y, BiFunction<Integer, Integer, Integer> neighbourCounter) {
        return layout[y][x] == CHAIR && neighbourCounter.apply(x, y) == 0;
    }

    private int countOccupiedAdjacent(int x, int y) {
        int count = 0;
        for (int i = 0; i < MAX_NEIGH; i++) {
            int neighX = x + dx[i];
            int neighY = y + dy[i];
            if (isInRange(neighX, width - 1) && isInRange(neighY, height - 1) && layout[neighY][neighX] == PERSON) {
                count++;
            }
        }
        return count;
    }

    private boolean isInRange(int coord, int max) {
        return coord >= 0 && coord <= max;
    }

    private void loadLayout(List<String> lines) {
        width = lines.get(0).length();
        height = lines.size();
        backup = new char[height][width];
        layout = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                layout[i][j] = lines.get(i).charAt(j);
            }
        }
    }

    private void printLayout() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(layout[i][j]);
            }
            System.out.println();
        }
    }
}
