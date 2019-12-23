package year2019;

import aoc.IAocTask;

import java.util.List;

public class Day18 implements IAocTask {
    String[][] maze;

    @Override
    public String getFileName() {
        return "aoc2019/input_18.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        maze = loadMaze(lines);
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    private String[][] loadMaze(List<String> lines) {
        String[][] maze = new String[lines.size()][lines.get(0).length()];

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                maze[i][j] = lines.get(i).substring(j, j + 1);
            }
        }
        return maze;
    }
}
