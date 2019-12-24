package year2019;

import aoc.IAocTask;
import year2019.utils.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day18 implements IAocTask {
    public static final int IDX_GATES = 0;
    public static final int IDX_KEYS = 1;
    public static final int IDX_PATH_COST = 2;

    private String[][] maze;
    private Set<String> allKeys;
    private static final List<Pair<Integer>> MOVES = createMoves();
    int MAX_STEPS = 900;
    private static final boolean printEnabled = true;
    private int currentBestPath = Integer.MAX_VALUE;
    private List<String> keyPath;

    private int[][][] pathCost;

    @Override
    public String getFileName() {
//        return "aoc2019/input_18.txt"; // TODO detect cycles
        return "aoc2019/input_18_small_81.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadMaze(lines);
        long start = System.nanoTime();
        System.out.println("START: " + getTimestamp());
        int fewestSteps = collectAllKeys();
        System.out.printf("fewest steps to collect all keys: %d%n", fewestSteps);
        System.out.printf("found path: %s%n", keyPath);
        System.out.println("END: " + getTimestamp());
        long end = System.nanoTime();
        System.out.printf("time elapsed [min]: %.3f%n", (end - start) / (1e9 * 60));
    }

    private String getTimestamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    private int collectAllKeys() {
        HashSet<String> foundKeys = new HashSet<>();
        List<String> keyPath = new ArrayList<>();

        Pair<Integer> startPosition = findMazeStartPosition();
        currentBestPath = Integer.MAX_VALUE;
        int stepsCount = 0;
        pathCost[startPosition.y][startPosition.x][IDX_PATH_COST] = stepsCount;
        pathCost[startPosition.y][startPosition.x][IDX_KEYS] = -1;
        pathCost[startPosition.y][startPosition.x][IDX_GATES] = 0;
        List<Pair<Integer>> possibleNextPositions = getPossibleNextPositions(null, startPosition, foundKeys, new HashSet<>(), stepsCount);

        printBoardWithCurrentPositionIfEnabled(startPosition, foundKeys, new HashSet<>(), stepsCount);

        for (Pair<Integer> position: possibleNextPositions) {
            HashSet<String> openedGatesCopy = new HashSet<>();
            HashSet<String> foundKeysCopy = new HashSet<>(foundKeys);

            Pair<Integer> prevPosition = new Pair<>(startPosition);
            int stepsToOpenAllGates = getStepsToOpenAllGates(prevPosition, position, foundKeysCopy, openedGatesCopy, stepsCount, keyPath);

            if (stepsToOpenAllGates < currentBestPath) {
                currentBestPath = stepsToOpenAllGates;
                this.keyPath = keyPath;
            }
        }
        return currentBestPath;
    }

    private void printBoardWithCurrentPositionIfEnabled(Pair<Integer> currentPosition, HashSet<String> foundKeys, HashSet<String> openedGates, int steps) {
        if (!printEnabled) {
            return;
        }
        System.out.println();
        String footer = "@(%2d, %2d)=%s\nkeys: %s,\nopened gates: %s\nsteps: %d";
        String tileValue = "X";
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                String mazeCell = maze[i][j];

                if (i == currentPosition.y && j == currentPosition.x) {
                    System.out.print("@");
                    tileValue = mazeCell.equals("@") || foundKeys.contains(mazeCell) || openedGates.contains(mazeCell) ? "." : mazeCell;
                } else if (mazeCell.equals("@")) {
                    System.out.print(".");
                } else {
                    System.out.print((foundKeys.contains(mazeCell) || openedGates.contains(mazeCell) ? "*" : mazeCell));
                }
            }
            System.out.println();
        }
        System.out.println(String.format(footer, currentPosition.x, currentPosition.y, tileValue, foundKeys, openedGates, steps));
    }

    private int getStepsToOpenAllGates(Pair<Integer> prevPosition, Pair<Integer> position, HashSet<String> foundKeys, HashSet<String> openedGates, int stepsToCurrentPosition, List<String> keyPath) {
        printBoardWithCurrentPositionIfEnabled(position, foundKeys, openedGates, stepsToCurrentPosition + 1);
        if (stepsToCurrentPosition > MAX_STEPS || currentBestPath < stepsToCurrentPosition) {
            return Integer.MAX_VALUE;
        }
        if (foundKeys.size() == allKeys.size()) {
//            if (printEnabled) {
                System.out.printf("[%s] steps to current position: %d\n", getTimestamp(), stepsToCurrentPosition);
//            }
            return stepsToCurrentPosition;
        }
        List<Pair<Integer>> possibleNextPositions = getPossibleNextPositions(prevPosition, position, foundKeys, openedGates, stepsToCurrentPosition);

        HashSet<String> openedGatesCopy = new HashSet<>(openedGates);
        HashSet<String> foundKeysCopy = new HashSet<>(foundKeys);
        ArrayList<String> keyPathCopy = new ArrayList<>(keyPath);
        String mazePlace = maze[position.y][position.x];
        if (isKeyLocation(mazePlace) && !foundKeys.contains(mazePlace)) {
            foundKeysCopy.add(mazePlace);
            keyPathCopy.add(mazePlace);
        } else if (isGateLocation(mazePlace)) {
            assert foundKeysCopy.contains(mazePlace.toLowerCase());
            openedGatesCopy.add(mazePlace);
        }

        for (Pair<Integer> nextPos: possibleNextPositions) {
            pathCost[nextPos.y][nextPos.x][IDX_KEYS] = foundKeys.size();
            pathCost[nextPos.y][nextPos.x][IDX_PATH_COST] = stepsToCurrentPosition + 1;
            Pair<Integer> prevPos = new Pair<>(position);
            int stepsToOpenAllGates = getStepsToOpenAllGates(prevPos, nextPos, foundKeysCopy, openedGatesCopy, stepsToCurrentPosition + 1, keyPathCopy);
            if (stepsToOpenAllGates < currentBestPath) {
                currentBestPath = stepsToOpenAllGates;
                this.keyPath = keyPathCopy;
            }
        }
        return currentBestPath;
    }

    private List<Pair<Integer>> getPossibleNextPositions(Pair<Integer> prevPosition, Pair<Integer> currentPosition, HashSet<String> foundKeys, HashSet<String> openedGates, int stepsCount) {
        List<Pair<Integer>> nextPositions = new ArrayList<>();
        for (Pair<Integer> move : MOVES) {
            if (isMovePossible(prevPosition, currentPosition, move, foundKeys, openedGates, stepsCount)) {
                nextPositions.add(new Pair<>(currentPosition.x + move.x, currentPosition.y + move.y));
            }
        }
        return nextPositions;
    }

    private boolean isMovePossible(Pair<Integer> prevPosition, Pair<Integer> currentPosition, Pair<Integer> move, HashSet<String> foundKeys, HashSet<String> openedGates, int stepsCount) {
        Pair<Integer> nextPosition = new Pair<>(currentPosition.x + move.x, currentPosition.y + move.y);
        String currentMazeCell = maze[currentPosition.y][currentPosition.x];
        String mazeCell = maze[nextPosition.y][nextPosition.x];

        if (nextPosition.equals(prevPosition) && !isNewKeyPosition(foundKeys, currentMazeCell)) {
            return false;
        }

        if (isGateLocation(mazeCell)) {
            return foundKeys.contains(mazeCell.toLowerCase());
        }

        int[] cost = pathCost[nextPosition.y][nextPosition.x];

        return isFree(mazeCell);// && openedGates.size() >= cost[IDX_GATES] && foundKeys.size() >= cost[IDX_KEYS] && stepsCount < cost[IDX_PATH_COST];
    }

    private boolean isNewKeyPosition(HashSet<String> foundKeys, String mazeCell) {
        return isKeyLocation(mazeCell) && !foundKeys.contains(mazeCell);
    }

    private Pair<Integer> findMazeStartPosition() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j].equals("@")) {
                    return new Pair<>(j, i);
                }
            }
        }
        throw new RuntimeException("Could not find the starting position");
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    private void loadMaze(List<String> lines) {
        maze = new String[lines.size()][lines.get(0).length()];
        allKeys = new HashSet<>();
        pathCost = new int[maze.length][maze[0].length][3];

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                pathCost[i][j][IDX_PATH_COST] = Integer.MAX_VALUE; // path to place
                pathCost[i][j][IDX_GATES] = -1; // opened gates size
                pathCost[i][j][IDX_KEYS] = -1; // found keys gates size

                maze[i][j] = lines.get(i).substring(j, j + 1);
                if (maze[i][j].matches("[a-z]")) {
                    allKeys.add(maze[i][j]);
                }
            }
        }
    }

    private static List<Pair<Integer>> createMoves() {
        List<Pair<Integer>> moves = new ArrayList<>();
        moves.add(new Pair<>(0, -1));
        moves.add(new Pair<>(1, 0));
        moves.add(new Pair<>(0, 1));
        moves.add(new Pair<>(-1, 0));
        return moves;
    }

    private boolean isGateLocation(String mazePlace) {
        return mazePlace.matches("[A-Z]");
    }

    private boolean isKeyLocation(String mazePlace) {
        return mazePlace.matches("[a-z]");
    }

    private boolean isFree(String mazeCell) {
        return mazeCell.matches("[a-z]|\\.|@");
    }
}
