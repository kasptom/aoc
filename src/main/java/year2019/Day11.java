package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Day11 implements IAocTask {
    private int operationsCount = 0;
    HashSet<Day10.Point> paintedPanels = new HashSet<>();
    DirNode currentDirection;

    int instructionPointer = 0;
    Day10.Point currentPosition;
    boolean finish;

    int squareSize = 200;
    int[][] visited;
    int middleIdx = squareSize / 2;

    int LEFT = 0;
    int RIGHT = 1;
    int BLACK = 0;
    int WHITE = 1;

    @Override
    public String getFileName() {
        return "aoc2019/input_11.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        currentPosition = new Day10.Point(0, 0);
        currentPosition.color = 0;
        visited = new int[squareSize][squareSize];
        solve(lines);
//        printColors();
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        currentPosition = new Day10.Point(0, 0);
        currentPosition.color = 1;
        solve(lines);
        printColors();
    }

    private void solve(List<String> lines) {
        finish = false;
        currentDirection = createDirectionNodes();
        visited = new int[squareSize][squareSize];
        paintedPanels.clear();
        instructionPointer = 0;
        operationsCount = 0;

        Day10.Point.showColor = true;
        long[] parsedCode = Aoc2019Utils.loadProgram(lines);
        long[] largerMemory = new long[6000];
        System.arraycopy(parsedCode, 0, largerMemory, 0, parsedCode.length);
        parsedCode = largerMemory;

        Day05 intComp = new Day05();
        intComp.setIoListeners(this::inputCurrentPanelColor, (value, ip) -> {
            instructionPointer = ip;
            if (operationsCount % 2 == 0) {
                paintPanel(value);
            } else {
                turnAndMoveForward(value);
            }
            operationsCount++;
            return true;
        }, () -> finish = true);

        while (!finish) {
            intComp.runProgram(parsedCode, new long[2], instructionPointer);
        }
        List<Day10.Point> panels = new ArrayList<>(paintedPanels);
        panels.sort(Day10.Point::compareTo);
        System.out.println(panels);
        System.out.println(operationsCount / 2);
        System.out.println(paintedPanels.size());
        //printPanels();
    }

    @SuppressWarnings("unused")
    private void printColors() {
        for (int i = -squareSize / 2; i < squareSize / 2; i++) {
            for (int j = -squareSize; j < squareSize / 2; j++) {
                Day10.Point point = new Day10.Point(j, -i);
                if (paintedPanels.contains(point)) {
                    Day10.Point painted = paintedPanels.stream().filter(point::equals).findFirst().orElse(null);
                    assert painted != null;
                    System.out.printf("%s", painted.color == WHITE ? "▮" : "▯");
                }
                else {
                    System.out.print("▯");
                }
            }
            System.out.println();
        }
    }

    private void turnAndMoveForward(long value) {
        Day10.Point prevPosition = new Day10.Point(currentPosition.x, currentPosition.y);
        prevPosition.color = currentPosition.color;

        currentDirection = value == LEFT
                ? currentDirection.left : value == RIGHT
                ? currentDirection.right : null;
        assert currentDirection != null;

        System.out.print(prevPosition);
        System.out.printf(" turning %s: %s ", value == LEFT ? "L" : "R", currentDirection);
        currentPosition = new Day10.Point(currentPosition.x + currentDirection.dirX, currentPosition.y + currentDirection.dirY);
        if (paintedPanels.contains(currentPosition)) {
//            System.out.printf("contains: %s (%s)%n", currentPosition, paintedPanels.stream().filter(p -> p.equals(currentPosition)).findFirst().orElse(null));
            currentPosition.color = Objects
                    .requireNonNull(paintedPanels.stream()
                            .filter(point -> currentPosition.equals(point))
                            .findFirst()
                            .orElse(null)).color;
        }
        System.out.println(currentPosition);
    }

    private void paintPanel(long value) {
        System.out.printf(", painting: %s ", getColorName((int) value));
        Day10.Point point = new Day10.Point(currentPosition.x, currentPosition.y);
        point.color = (int) value;
        currentPosition.color = (int) value;
        if (paintedPanels.contains(point)) {
            Day10.Point toPaint = paintedPanels.stream().filter(p -> p.equals(point)).findFirst().orElse(null);
            assert toPaint != null;
            toPaint.color = point.color;
        } else {
            paintedPanels.add(point);
        }
        visited[middleIdx - point.y][middleIdx + point.x]++;
//        printPanels();
    }

    @SuppressWarnings("unused")
    private void printPanels() {
        for (int i = 0; i < squareSize; i++) {
            for (int j = 0; j < squareSize; j++) {
                System.out.printf("%s", visited[i][j] > 0 ? "▮" : "▯");
            }
            System.out.println();
        }
    }

    private long inputCurrentPanelColor() {
        System.out.printf("detected color: %s ", getColorName(currentPosition.color));
        return currentPosition.color;
    }

    private String getColorName(int color) {
        return color == WHITE ? "white" : color == BLACK ? "black" : "--- UNKNOWN";
    }

    static class DirNode {
        int dirX;
        int dirY;
        String symbol;
        DirNode right;
        DirNode left;

        @Override
        public String toString() {
            return String.format("{%3d, %3d %s}", dirX, dirY, symbol);
        }
    }

    private DirNode createDirectionNodes() {
        DirNode north = new DirNode();
        north.dirX = 0;
        north.dirY = 1;
        north.symbol = "^";

        DirNode west = new DirNode();
        west.dirX = -1;
        west.dirY = 0;
        west.symbol = "<";

        DirNode south = new DirNode();
        south.dirX = 0;
        south.dirY = -1;
        south.symbol = "v";

        DirNode east = new DirNode();
        east.dirX = 1;
        east.dirY = 0;
        east.symbol = ">";

        north.right = east;
        north.left = west;
        west.right = north;
        west.left = south;
        south.left = east;
        south.right = west;
        east.right = south;
        east.left = north;
        return  north;
    }
}
