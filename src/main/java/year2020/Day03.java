package year2020;

import aoc.IAocTask;

import java.util.List;

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
        int count = countTrees(area);
        System.out.println(count);
    }

    private int countTrees(char[][] area) {
        int rowIdx = 0;
        int colIdx = 0;
        int count = 0;
        int patternWidth = area[0].length;

        while (rowIdx < area.length) {
            if (area[rowIdx][colIdx] == TREE) {
                count++;
            }
            rowIdx += MOVE_Y;
            colIdx = (colIdx + MOVE_X) % patternWidth;
        }

        return count;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

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
}
