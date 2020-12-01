package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;
import year2019.utils.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Day18 implements IAocTask {
    private String[][] maze;
    private Set<String> allKeys;
    private static final List<Pair<Integer>> MOVES = Aoc2019Utils.createMoves();
    int MAX_STEPS = 900;
    private static final boolean printEnabled = false;
    private int currentBestPath = Integer.MAX_VALUE;
    private List<String> keyPath;

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
        List<Pair<Integer>> possibleNextPositions = getPossibleNextPositions(null, startPosition, foundKeys);

        printBoardWithCurrentPositionIfEnabled(startPosition, foundKeys, new HashSet<>(), stepsCount);

        for (Pair<Integer> position : possibleNextPositions) {
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
        List<Pair<Integer>> possibleNextPositions = getPossibleNextPositions(prevPosition, position, foundKeys);

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

        for (Pair<Integer> nextPos : possibleNextPositions) {
            Pair<Integer> prevPos = new Pair<>(position);
            int stepsToOpenAllGates = getStepsToOpenAllGates(prevPos, nextPos, foundKeysCopy, openedGatesCopy, stepsToCurrentPosition + 1, keyPathCopy);
            if (stepsToOpenAllGates < currentBestPath) {
                currentBestPath = stepsToOpenAllGates;
                this.keyPath = keyPathCopy;
            }
        }
        return currentBestPath;
    }

    private List<Pair<Integer>> getPossibleNextPositions(Pair<Integer> prevPosition, Pair<Integer> currentPosition, HashSet<String> foundKeys) {
        List<Pair<Integer>> nextPositions = new ArrayList<>();
        for (Pair<Integer> move : MOVES) {
            if (isMovePossible(prevPosition, currentPosition, move, foundKeys)) {
                nextPositions.add(new Pair<>(currentPosition.x + move.x, currentPosition.y + move.y));
            }
        }
        return nextPositions;
    }

    private boolean isMovePossible(Pair<Integer> prevPosition, Pair<Integer> currentPosition, Pair<Integer> move, HashSet<String> foundKeys) {
        Pair<Integer> nextPosition = new Pair<>(currentPosition.x + move.x, currentPosition.y + move.y);
        String currentMazeCell = maze[currentPosition.y][currentPosition.x];
        String mazeCell = maze[nextPosition.y][nextPosition.x];

        if (nextPosition.equals(prevPosition) && !isNewKeyPosition(foundKeys, currentMazeCell)) {
            return false;
        }

        if (isGateLocation(mazeCell)) {
            return foundKeys.contains(mazeCell.toLowerCase());
        }

        return isFree(mazeCell);
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

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                maze[i][j] = lines.get(i).substring(j, j + 1);
                if (maze[i][j].matches("[a-z]")) {
                    allKeys.add(maze[i][j]);
                }
            }
        }
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
