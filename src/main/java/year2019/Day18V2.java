package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;
import year2019.utils.Pair;

import java.util.*;

public class Day18V2 implements IAocTask {

    private static final List<Pair<Integer>> MOVES = Aoc2019Utils.createMoves();

    String[][] maze;
    HashSet<Pair<Integer>> vertices;
    HashMap<String, Pair<Integer>> keysAndGatesCoordinates;

    @Override
    public String getFileName() {
        return "aoc2019/input_18_small_136.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadMaze(lines);
        List<Path> shortestPaths = findShortestPath("@");
        List<Path> shortestPaths2 = findShortestPath("A");
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    private void loadMaze(List<String> lines) {
        int height = lines.size();
        int width = lines.get(0).length();
        maze = new String[height][width];
        vertices = new HashSet<>();
        keysAndGatesCoordinates = new HashMap<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = lines.get(i).substring(j, j + 1);
                if (!maze[i][j].equals("#")) {
                    vertices.add(new Pair<>(j, i));
                }
                if (maze[i][j].matches("[A-Z]|[a-z]|@")) {
                    keysAndGatesCoordinates.put(maze[i][j], new Pair<>(j, i));
                }
            }
        }
    }

    private List<Path> findShortestPath(String from) {
        HashMap<Pair<Integer>, Integer> unvisitedCost = new HashMap<>();
        vertices.forEach(vertex -> unvisitedCost.put(vertex, Integer.MAX_VALUE)); // all with max cost

        HashMap<Pair<Integer>, Pair<Integer>> childToParent = new HashMap<>();

        int pathCost = 0;
        Pair<Integer> currentPosition = keysAndGatesCoordinates.get(from);
        unvisitedCost.put(currentPosition, pathCost);

        while (!unvisitedCost.isEmpty() && currentPosition != null) {
            pathCost = unvisitedCost.remove(currentPosition);
            pathCost++;

            for (Pair<Integer> move : MOVES) {
                Pair<Integer> nextPosition = new Pair<>(currentPosition.x + move.x, currentPosition.y + move.y);
                Integer cost = unvisitedCost.get(nextPosition);
                if (cost != null && cost > pathCost) {
                    childToParent.put(nextPosition, currentPosition);
                    unvisitedCost.put(nextPosition, pathCost);
                }
            }

            currentPosition = getUnvisitedNodeWithLowestCost(unvisitedCost);
        }

        List<Path> paths = new ArrayList<>();
        // TODO rewrite from childToParent to paths, save the required keys
        return paths;
    }

    private Pair<Integer> getUnvisitedNodeWithLowestCost(HashMap<Pair<Integer>, Integer> unvisitedCost) {
        Integer lowestCost = unvisitedCost.values().stream().min(Integer::compareTo).orElse(Integer.MAX_VALUE);
        if (lowestCost == Integer.MAX_VALUE) {
            return null;
        }
        return unvisitedCost
                .keySet()
                .stream()
                .filter(key -> unvisitedCost.get(key).equals(lowestCost))
                .findFirst().orElse(null);
    }

    static class Path {
        String from;
        String to;
        List<Pair<Integer>> path;
        HashSet<String> requiredKeys;

        public Path(String from, String to) {
            this.from = from;
            this.to = to;
            path = new ArrayList<>();
            requiredKeys = new HashSet<>();
        }
    }
}
