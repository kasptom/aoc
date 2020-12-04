package year2020;

import aoc.IAocTask;

import java.util.List;
import java.util.stream.IntStream;

public class Day03 implements IAocTask {
    private static final int MOVE_X = 3;
    private static final int MOVE_Y = 1;
    private static final char TREE = '#';

    @Override
    public String getFileName() {
        return "aoc2020/input_03.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        char[][] area = loadArea(lines);
        int count = countTrees(area, MOVE_X, MOVE_Y);
        System.out.println(count);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        char[][] area = loadArea(lines);
        int[] slopeX = {1, 3, 5, 7, 1};
        int[] slopeY = {1, 1, 1, 1, 2};
        int product = IntStream.range(0, slopeX.length)
                .map(idx -> countTrees(area, slopeX[idx], slopeY[idx]))
                .reduce(1, (x, y) ->x * y);
        System.out.println(product);
    }

    private char[][] loadArea(List<String> lines) {
        int rowsCount = lines.size();
        int columnsCount = lines.get(0).length();
        char[][] area = new char[rowsCount][columnsCount];
        for (int i = 0; i < rowsCount; i++) {
            String row = lines.get(i);
            for (int j = 0; j < columnsCount; j++) {
                area[i][j] = row.charAt(j);
            }
        }
        return area;
    }

    private int countTrees(char[][] area, int moveX, int moveY) {
        int rowIdx = 0;
        int colIdx = 0;
        int count = 0;
        int patternWidth = area[0].length;

        while (rowIdx < area.length) {
            if (area[rowIdx][colIdx] == TREE) {
                count++;
            }
            rowIdx += moveY;
            colIdx = (colIdx + moveX) % patternWidth;
        }

        return count;
    }
}
