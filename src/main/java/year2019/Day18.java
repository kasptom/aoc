package year2019;

import aoc.IAocTask;
import year2019.utils.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day18 implements IAocTask {
    private String[][] maze;
    private Set<String> allGates;
    private static final List<Pair<Integer>> MOVES = createMoves();

    @Override
    public String getFileName() {
        return "aoc2019/input_18.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadMaze(lines);
        int fewestSteps = unlockAllGates();
        System.out.printf("fewest steps to open all gates: %d%n", fewestSteps);
    }

    private int unlockAllGates() {
        HashSet<String> openedGates = new HashSet<>();
        HashSet<String> foundKeys = new HashSet<>();

        Pair<Integer> startPosition = findMazeStartPosition();
        int shortestPath = Integer.MAX_VALUE;
        List<Pair<Integer>> possibleNextPositions = getPossibleNextPositions(null, startPosition, foundKeys, openedGates);
        int stepsCount = 0;

        for (Pair<Integer> position: possibleNextPositions) {
            HashSet<String> openedGatesCopy = new HashSet<>(openedGates);
            HashSet<String> foundKeysCopy = new HashSet<>(foundKeys);
            String mazePlace = maze[position.y][position.x];
            if (isKeyLocation(mazePlace)) {
                openedGatesCopy.add(mazePlace);
            } else if (isGateLocation(mazePlace)) {
                assert foundKeysCopy.contains(mazePlace.toLowerCase());
                openedGatesCopy.add(mazePlace);
            }
            Pair<Integer> prevPosition = new Pair<>(startPosition);
            int stepsToOpenAllGates = getStepsToOpenAllGates(prevPosition, position, foundKeysCopy, openedGatesCopy, stepsCount + 1);
            if (stepsToOpenAllGates < shortestPath) {
                shortestPath = stepsToOpenAllGates;
            }
        }
        return shortestPath;
    }

    private int getStepsToOpenAllGates(Pair<Integer> prevPosition, Pair<Integer> position, HashSet<String> foundKeys, HashSet<String> openedGates, int stepsToCurrentPosition) {
        if (openedGates.size() == allGates.size()) {
            return stepsToCurrentPosition;
        }
        List<Pair<Integer>> possibleNextPositions = getPossibleNextPositions(prevPosition, position, foundKeys, openedGates);
        int shortestPath = Integer.MAX_VALUE;
        for (Pair<Integer> nextPos: possibleNextPositions) {
            HashSet<String> openedGatesCopy = new HashSet<>(openedGates);
            HashSet<String> foundKeysCopy = new HashSet<>(foundKeys);
            String mazePlace = maze[nextPos.y][nextPos.x];
            if (isKeyLocation(mazePlace)) {
                openedGatesCopy.add(mazePlace);
            } else if (isGateLocation(mazePlace)) {
                assert foundKeysCopy.contains(mazePlace.toLowerCase());
                openedGatesCopy.add(mazePlace);
            }
            Pair<Integer> prevPos = new Pair<>(position);
            int stepsToOpenAllGates = getStepsToOpenAllGates(prevPos, nextPos, foundKeysCopy, openedGatesCopy, stepsToCurrentPosition + 1);
            if (stepsToOpenAllGates < shortestPath) {
                shortestPath = stepsToOpenAllGates;
            }
        }
        return shortestPath;
    }

    private List<Pair<Integer>> getPossibleNextPositions(Pair<Integer> prevPosition, Pair<Integer> currentPosition, HashSet<String> foundKeys, HashSet<String> openedGates) {
        List<Pair<Integer>> nextPositions = new ArrayList<>();
        for (Pair<Integer> move : MOVES) {
            if (isMovePossible(prevPosition, currentPosition, move, foundKeys)) {
                nextPositions.add(new Pair<>(currentPosition.x + move.x, currentPosition.y + move.x));
            }
        }
        return nextPositions;
    }

    private boolean isMovePossible(Pair<Integer> prevPosition, Pair<Integer> currentPosition, Pair<Integer> move, HashSet<String> foundKeys) {
        Pair<Integer> nextPosition = new Pair<>(currentPosition.x + move.x, currentPosition.y + move.y);
        if (nextPosition.equals(prevPosition)) {
            return false;
        }
        String mazeCell = maze[nextPosition.y][nextPosition.x];

        if (isGateLocation(mazeCell)) {
            return foundKeys.contains(mazeCell.toLowerCase());
        }

        return isFree(mazeCell);
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
        allGates = new HashSet<>();

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                maze[i][j] = lines.get(i).substring(j, j + 1);
                if (maze[i][j].matches("[A-Z]")) {
                    allGates.add(maze[i][j]);
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
