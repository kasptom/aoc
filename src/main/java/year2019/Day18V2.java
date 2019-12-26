package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;
import year2019.utils.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Day18V2 implements IAocTask {

    private static final List<Pair<Integer>> MOVES = Aoc2019Utils.createMoves();

    String[][] maze;
    HashSet<Pair<Integer>> vertices;
    HashMap<String, Pair<Integer>> keysAndGatesCoordinates;
    HashMap<Pair<Integer>, String> coordinatesToKeysAndGates;

    @Override
    public String getFileName() {
        return "aoc2019/input_18_small_136.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadMaze(lines);
        Map<String, List<Path>> keyOrGateToPath = keysAndGatesCoordinates
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        keyValue -> findShortestPaths(keyValue.getKey())
                ));
        keyOrGateToPath.forEach((key, value) -> {
            System.out.printf("%n---Paths from: %s---%n", key);
            value.forEach(System.out::println);
        });
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
        coordinatesToKeysAndGates = new HashMap<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = lines.get(i).substring(j, j + 1);
                if (!maze[i][j].equals("#")) {
                    vertices.add(new Pair<>(j, i));
                }
                if (maze[i][j].matches("[A-Z]|[a-z]|@")) {
                    keysAndGatesCoordinates.put(maze[i][j], new Pair<>(j, i));
                    coordinatesToKeysAndGates.put(new Pair<>(j, i), maze[i][j]);
                }
            }
        }
    }

    /**
     * Finds the shortest paths (Dijkstra) from the root to all other nodes in the grid based labyrinth.
     * Collects the data about found keys
     *
     * @param root root of the paths
     * @return paths
     */
    private List<Path> findShortestPaths(String root) {
        HashMap<Pair<Integer>, Integer> unvisitedCost = new HashMap<>();
        vertices.forEach(vertex -> unvisitedCost.put(vertex, Integer.MAX_VALUE)); // all with max cost

        HashMap<Pair<Integer>, Pair<Integer>> childToParent = new HashMap<>();

        int pathCost = 0;
        Pair<Integer> currentPosition = keysAndGatesCoordinates.get(root);
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

        return mapToPaths(root, childToParent);
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

    /**
     * Each path starts with the root and ends with one of the coordinates in {@link Day18V2#keysAndGatesCoordinates}
     *
     * @param root          name of the root node
     * @param childToParent shortest paths from the root to all other available non-wall nodes
     * @return list of paths that end with key or gate (or @)
     */
    private List<Path> mapToPaths(String root, HashMap<Pair<Integer>, Pair<Integer>> childToParent) {
        return keysAndGatesCoordinates
                .keySet()
                .stream()
                .filter(keyOrGate -> !keyOrGate.equals(root))
                .map(keyOrGate -> {
                    Path path = new Path(root, keyOrGate);
                    Pair<Integer> rootCoordinates = keysAndGatesCoordinates.get(root);
                    Pair<Integer> currentCoordinates = keysAndGatesCoordinates.get(keyOrGate);
                    while (!currentCoordinates.equals(rootCoordinates)) {
                        path.path.add(currentCoordinates);
                        if (coordinatesToKeysAndGates.containsKey(currentCoordinates)) {
                            path.keysAndGatesOrdered.add(coordinatesToKeysAndGates.get(currentCoordinates));
                        }
                        currentCoordinates = childToParent.get(currentCoordinates);
                    }
                    Collections.reverse(path.path);
                    Collections.reverse(path.keysAndGatesOrdered);
                    return path;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    static class Path {
        String from;
        String to;
        List<Pair<Integer>> path;
        List<String> keysAndGatesOrdered;

        public Path(String from, String to) {
            this.from = from;
            this.to = to;
            path = new ArrayList<>();
            keysAndGatesOrdered = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "Path{" +
                    "from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", path=" + path +
                    ", keysAndGatesOrdered=" + keysAndGatesOrdered +
                    '}';
        }
    }
}
