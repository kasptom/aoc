package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.util.*;
import java.util.stream.Collectors;

import static year2019.Day15.Direction.*;
import static year2019.Day15.DroidConstants.*;

public class Day15 implements IAocTask {
    String[][] droidMap;
    int[][] shortestPathToPlace;
    Checkpoint processedCheckpoint;
    Checkpoint bestO2Checkpoint;

    boolean isPrintEnabled = false;
    @SuppressWarnings("unused")
    private MapPreview preview = new MapPreview();

    @Override
    public String getFileName() {
        return "aoc2019/input_15.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        long[] program = Aoc2019Utils.loadProgram(lines);
        solve(program, new int[]{0, 0});
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        solve(bestO2Checkpoint.code, bestO2Checkpoint.nextPosition);
        bestO2Checkpoint = null;
        int time = findTimeToFillUpWithOxygen();
        System.out.println("o2 spread time " + time);
    }

    private void solve(long[] program, int[] startPosition) {
        droidMap = new String[MAP_SIZE][MAP_SIZE];
        shortestPathToPlace = initializeShortestPathToPlace(startPosition);

        List<Checkpoint> checkpoints = new ArrayList<>();
        final boolean[] foundOxygen = {false};

        program = Aoc2019Utils.copyToLargerMemory(program, 6000);

        for (Direction direction : DIRECTION_TO_MOVE.keySet()) {
            int[] nextPosition = getNextPosition(startPosition, direction);
            checkpoints.add(new Checkpoint(0, program, startPosition, nextPosition, direction, 1));
        }

        Day05 intComp = new Day05();
        intComp.setIoListeners(() -> {
            if (isPrintEnabled) {
                System.out.printf("direction: %s , ", DIRECTION_TO_LETTER.get(processedCheckpoint.direction));
                System.out.println(processedCheckpoint);
            }
//            scanner.nextLine();
            return processedCheckpoint.direction.idx;
        }, (event, iPointer) -> {
            processedCheckpoint.instructionPointer = iPointer;
            List<Direction> possibleDirections = tryToMoveAndGetNextSteps(event, foundOxygen);

            List<Checkpoint> newCheckpoints = possibleDirections.stream()
                    .map(direction -> {
                        int[] newNext = getNextPosition(processedCheckpoint.nextPosition, direction);
                        return new Checkpoint(iPointer, processedCheckpoint.code, processedCheckpoint.nextPosition, newNext, direction, processedCheckpoint.pathLength + 1);
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            checkpoints.addAll(newCheckpoints);
            if (isPrintEnabled) {
                System.out.println("added checkpoints:" + newCheckpoints);
                System.out.println("possible moves: " + possibleDirections);
            }

            if (isPrintEnabled) {
                printMap();
                System.out.println(processedCheckpoint);
            }

            if (possibleDirections.isEmpty()) {
                return true;
            }

            foundOxygen[0] = event == EV_OXYGEN;
            return foundOxygen[0]; // pause if found the oxygen
        }, () -> System.out.println("this will never be printed"));

        while (!checkpoints.isEmpty()) {
            processedCheckpoint = checkpoints.remove(0);

            intComp.runProgram(processedCheckpoint.code, new long[2], 0);
            if (foundOxygen[0]) {
                if (bestO2Checkpoint == null || bestO2Checkpoint.pathLength > processedCheckpoint.pathLength) {
                    bestO2Checkpoint = processedCheckpoint;
                    processedCheckpoint.stepsToReachOxygen = processedCheckpoint.pathLength;
                }
                System.out.println("---");
                System.out.printf("O2 found %s%n", processedCheckpoint);
                System.out.printf("best O2 %s%n", bestO2Checkpoint);
                System.out.println("---");
            }
        }
        droidMap[MAP_SIZE / 2][MAP_SIZE / 2] = "$";
        System.out.println(preview);
        printShortestPaths();
        System.out.println(bestO2Checkpoint);
    }

    private int[] getNextPosition(int[] startPosition, Direction direction) {
        int[] nextPosition = new int[2];
        int[] move = DIRECTION_TO_MOVE.get(direction);
        nextPosition[0] = startPosition[0] + move[0];
        nextPosition[1] = startPosition[1] + move[1];
        return nextPosition;
    }

    private void printShortestPaths() {
        printMargin();
        for (int i = 0; i < MAP_SIZE; i++) {
            System.out.print("|");
            for (int j = 0; j < MAP_SIZE; j++) {
                if (shortestPathToPlace[j][i] != Integer.MAX_VALUE) {
                    System.out.printf("%3d ", shortestPathToPlace[j][i]);
                } else {
                    System.out.print("XXX ");
                }
            }
            System.out.print("|");
            System.out.println();
        }
        printMargin();
    }

    private void printMargin() {
        System.out.print("|");
        for (int j = 0; j < MAP_SIZE; j++) {
            System.out.print("--- ");
        }
        System.out.println("|");
    }

    private int[][] initializeShortestPathToPlace(int[] startPosition) {
        int[][] records = new int[MAP_SIZE][MAP_SIZE];
        for (int[] row : records) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        records[MAP_SIZE / 2 + startPosition[0]][MAP_SIZE / 2 + startPosition[1]] = 0;
        return records;
    }

    private void printMap() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                System.out.print(droidMap[j][i] != null ? droidMap[j][i] : " ");
            }
            System.out.println();
        }
    }

    private void updateMap(int[] xy, long event) {
        droidMap[MAP_SIZE / 2 + xy[0]][MAP_SIZE / 2 + xy[1]] = DroidConstants.getEventSymbol((int) event);
    }

    private List<Direction> tryToMoveAndGetNextSteps(long event, boolean[] foundOxygen) {
        if (event == EV_WALL_HIT) {
            removeRobotSymbolAt(processedCheckpoint.currPosition);
            updateMap(processedCheckpoint.nextPosition, event);
            return new ArrayList<>();
        }

        if (event == EV_OXYGEN && updateIfIsShorter(processedCheckpoint)) {
            droidMap[MAP_SIZE / 2][MAP_SIZE / 2] = IC_START;
            System.out.println("oxygen detected");
            removeRobotSymbolAt(processedCheckpoint.currPosition);
            updateMap(processedCheckpoint.nextPosition, event);
            foundOxygen[0] = true;
            return new ArrayList<>();
        }

        if (event == EV_MOVE_OK && updateIfIsShorter(processedCheckpoint)) {
            removeRobotSymbolAt(processedCheckpoint.currPosition);
            return lookAroundAndGetTheSteps();
        }
        return new ArrayList<>();
    }

    private boolean updateIfIsShorter(Checkpoint processedCheckpoint) {
        int[] yx = processedCheckpoint.nextPosition;
        boolean isShorter = processedCheckpoint.pathLength < shortestPathToPlace[MAP_SIZE / 2 + yx[0]][MAP_SIZE / 2 + yx[1]];
        if (isShorter) {
            shortestPathToPlace[MAP_SIZE / 2 + yx[0]][MAP_SIZE / 2 + yx[1]] = processedCheckpoint.pathLength;
        }
        return isShorter;
    }

    private List<Direction> lookAroundAndGetTheSteps() {
        HashSet<Direction> options = new HashSet<>();

        int pathLength = processedCheckpoint.pathLength + 1;
        int[] newStart = processedCheckpoint.nextPosition;

        for (Direction direction : DIRECTION_TO_MOVE.keySet()) {
            int[] move = DIRECTION_TO_MOVE.get(direction);
            int x = MAP_SIZE / 2 + newStart[1] + move[1];
            int y = MAP_SIZE / 2 + newStart[0] + move[0];
            String newMapPlace = droidMap[y][x];

            if (pathLength < shortestPathToPlace[y][x] && (newMapPlace == null || newMapPlace.equals(IC_VISITED))) {
                options.add(direction);
            }
        }
        // no empty place found

//        if (options.size() == 0) {
//            throw new RuntimeException("No option found");
//        }
        return new ArrayList<>(options);
    }

    private void removeRobotSymbolAt(int[] currPosition) {
        droidMap[MAP_SIZE / 2 + currPosition[0]][MAP_SIZE / 2 + currPosition[1]] = IC_VISITED;
    }

    private int findTimeToFillUpWithOxygen() {
        int longestTime = 0;
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                int currentPointO2FillTime = shortestPathToPlace[i][j];
                if (currentPointO2FillTime != Integer.MAX_VALUE) {
                    longestTime = Math.max(longestTime, currentPointO2FillTime);
                }
            }
        }
        return longestTime;
    }

    static class DroidConstants {
        static final HashMap<Direction, int[]> DIRECTION_TO_MOVE = new HashMap<>();
        static final HashMap<Direction, Direction> DIRECTION_TO_OPPOSITE = new HashMap<>();
        static final HashMap<Direction, String> DIRECTION_TO_LETTER = new HashMap<>();

        static {
            DIRECTION_TO_MOVE.put(NORTH, new int[]{0, -1});
            DIRECTION_TO_MOVE.put(SOUTH, new int[]{0, 1});
            DIRECTION_TO_MOVE.put(WEST, new int[]{-1, 0});
            DIRECTION_TO_MOVE.put(EAST, new int[]{1, 0});

            DIRECTION_TO_OPPOSITE.put(NORTH, SOUTH);
            DIRECTION_TO_OPPOSITE.put(SOUTH, NORTH);
            DIRECTION_TO_OPPOSITE.put(WEST, EAST);
            DIRECTION_TO_OPPOSITE.put(EAST, WEST);

            DIRECTION_TO_LETTER.put(NORTH, "N");
            DIRECTION_TO_LETTER.put(SOUTH, "S");
            DIRECTION_TO_LETTER.put(EAST, "E");
            DIRECTION_TO_LETTER.put(WEST, "W");
        }

        static final int EV_WALL_HIT = 0;
        static final int EV_MOVE_OK = 1;
        static final int EV_OXYGEN = 2;

        static String IC_WALL_HIT = "#";
        static String IC_MOVE_OK = " ";
        static String IC_OXYGEN = "O";
        static String IC_ROBOT = "D";
        static String IC_VISITED = "X";
        static String IC_START = "$";

        static int MAP_SIZE = 50;

        public static String getEventSymbol(int event) {
            switch (event) {
                case EV_WALL_HIT:
                    return IC_WALL_HIT;
                case EV_MOVE_OK:
                    return IC_MOVE_OK;
                case EV_OXYGEN:
                    return IC_OXYGEN;
                default:
                    return "UNKNOWN";
            }
        }
    }

    static class Checkpoint {
        int instructionPointer;
        long[] code;

        int[] currPosition;
        int[] nextPosition;
        private Direction direction;

        int stepsToReachOxygen; // can be -1
        int pathLength;

        @Override
        public String toString() {
            return "{" +
                    "IP=" + instructionPointer +
                    ", " + Arrays.toString(currPosition) + "->" + Arrays.toString(nextPosition) +
                    ", " + direction +
                    ", O2=" + stepsToReachOxygen +
                    ", PTH=" + pathLength +
                    '}';
        }

        public Checkpoint(int instructionPointer, long[] code, int[] currPosition, int[] nextPosition, Direction direction, int pathLength) {
            this.instructionPointer = instructionPointer;
            this.code = new long[code.length];
            this.direction = direction;
            System.arraycopy(code, 0, this.code, 0, code.length);
            this.currPosition = new int[2];
            this.nextPosition = new int[2];
            System.arraycopy(currPosition, 0, this.currPosition, 0, 2);
            System.arraycopy(nextPosition, 0, this.nextPosition, 0, 2);
            stepsToReachOxygen = -1;
            this.pathLength = pathLength;
        }
    }

    enum Direction {
        NORTH(1), SOUTH(2), EAST(3), WEST(4);
        int idx;

        Direction(int idx) {
            this.idx = idx;
        }
    }

    class MapPreview {
        @Override
        public String toString() {
            StringBuilder mapStr = new StringBuilder();
            printMargin(mapStr);
            mapStr.append("\n");
            for (int i = 0; i < MAP_SIZE; i++) {
                mapStr.append("|");
                for (int j = 0; j < MAP_SIZE; j++) {
                    mapStr.append(droidMap[j][i] != null ? String.format("%3s ", droidMap[j][i]) : "    ");
                }
                mapStr.append("|");
                mapStr.append("\n");
            }
            printMargin(mapStr);
            return mapStr.toString();
        }

        private void printMargin(StringBuilder mapStr) {
            mapStr.append("|");
            for (int i = 0; i < MAP_SIZE; i++) {
                mapStr.append("--- ");
            }
            mapStr.append("|");
        }
    }
}
