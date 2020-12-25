package year2020;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Day05 implements IAocTask {
    private static final int ROW_COUNT = 128;
    private static final int COL_COUNT = 8;
    private static final int FB_MOVES_COUNT = 7;
    private static final int LR_MOVES_COUNT = 3;
    private static final char BACK = 'B';
    //    private static final char FRONT = 'F';
    //    private static final char LEFT = 'L';
    private static final char RIGHT = 'R';
    TreeSet<Integer> seatIds = new TreeSet<>();

    @Override
    public String getFileName() {
        return "aoc2020/input_05.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int maxId = lines.stream()
                .map(Day05::toId)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(-1);

        System.out.println(maxId);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        lines.stream()
                .map(Day05::toId)
                .mapToInt(Integer::intValue)
                .forEach(seatIds::add);

        var seatsSorted = new ArrayList<>(seatIds);
        for (int i = 1; i < seatsSorted.size(); i++) {
            if (seatsSorted.get(i - 1) + 1 != seatsSorted.get(i)) {
                System.out.println(seatsSorted.get(i - 1) + 1);
                break;
            }
        }
    }

    private static int toId(String boardingCode) {
        char[] moves = new char[FB_MOVES_COUNT + LR_MOVES_COUNT];
        for (int i = 0; i < FB_MOVES_COUNT + LR_MOVES_COUNT; i++) {
            moves[i] = boardingCode.charAt(i);
        }

        int row;
        int col;
        row = update(moves, FB_MOVES_COUNT, BACK, ROW_COUNT, 0);
        col = update(moves, LR_MOVES_COUNT, RIGHT, COL_COUNT, FB_MOVES_COUNT);
        return rowColToId(row, col);
    }

    private static int update(char[] moves, int codeSize, char ascMove, int dimSize, int codeOffset) {
        int coordinate = 0;
        int divisor = 2;
        for (int i = codeOffset; i < codeOffset + codeSize; i++) {
            if (moves[i] == ascMove) {
                coordinate += dimSize / divisor;
            }
            divisor *= 2;
        }
        return coordinate;
    }

    private static int rowColToId(int row, int col) {
        return COL_COUNT * row + col;
    }
}
