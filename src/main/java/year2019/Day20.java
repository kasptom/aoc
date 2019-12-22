package year2019;

import aoc.IAocTask;
import year2019.utils.Pair;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class Day20 implements IAocTask {

    String[][] maze;
    int[][] shortestPathTo;
    int[] dx = {0, 1, 0, -1};
    int[] dy = {-1, 0, 1, 0};
    int recursionDepthLimit = 50;
    private boolean printEnabled = false;

    @SuppressWarnings("unused")
    MazePreview preview = new MazePreview();
    private static final String START = "AA";
    private static final String END = "ZZ";
    HashMap<String, List<Pair<Integer>>> portalNameToLocations;
    private HashMap<Pair<Integer>, String> positionToPortalName;

    HashSet<Pair<Integer>> entrances;
    HashSet<Pair<Integer>> exits;

    private static final String[][] UPPER_OUTER_CORNER = {{" ", " "}, {" ", "#"}};
    private static final String[][] LOWER_OUTER_CORNER = {{"#", " "}, {" ", " "}};
    private static final String[][] UPPER_INNER_CORNER = {{"#", "#"}, {"#", " "}};
    private static final String[][] LOWER_INNER_CORNER = {{" ", "#"}, {"#", "#"}};

    @Override
    public String getFileName() {
        return "aoc2019/input_20.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        maze = loadMaze(lines);
        MazeMetadata metadata = getMazeMetadata(maze);

        initializePortalNameToLocations(maze, metadata);

        int pathLength = getShortestPath(maze, portalNameToLocations);
        System.out.printf("Shortest path length: %d%n", pathLength);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        maze = loadMaze(lines);
        MazeMetadata metadata = getMazeMetadata(maze);

        initializePortalNameToLocations(maze, metadata);

        int pathLength = getShortestRecursivePath(maze, portalNameToLocations);
        System.out.printf("Shortest recursive path length: %d%n", pathLength);
    }

    private MazeMetadata getMazeMetadata(String[][] maze) {
        MazeMetadata metadata = new MazeMetadata();
        for (int i = 2; i < maze.length - 1; i++) {
            for (int j = 2; j < maze[0].length - 1; j++) {
                if (isCornerV2(i, j, maze, UPPER_OUTER_CORNER)) {
                    metadata.upperOuterCorner.x = j;
                    metadata.upperOuterCorner.y = i;
                } else if (isCornerV1(i, j, maze, LOWER_OUTER_CORNER)) {
                    metadata.lowerOuterCorner.x = j;
                    metadata.lowerOuterCorner.y = i;
                } else if (isCornerV1(i, j, maze, UPPER_INNER_CORNER)) {
                    metadata.upperInnerCorner.x = j;
                    metadata.upperInnerCorner.y = i;
                } else if (isCornerV2(i, j, maze, LOWER_INNER_CORNER)) {
                    metadata.lowerInnerCorner.x = j;
                    metadata.lowerInnerCorner.y = i;
                }
            }
        }
        return metadata;
    }

    private boolean isCornerV1(int i, int j, String[][] maze, String[][] corner) {
        for (int m = 0; m < 2; m++) {
            for (int n = 0; n < 2; n++) {
                if (!maze[i + m][j + n].equals(corner[m][n])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isCornerV2(int i, int j, String[][] maze, String[][] corner) {
        for (int m = 0; m < 2; m++) {
            for (int n = 0; n < 2; n++) {
                if (!maze[i - 1 + m][j - 1 + n].equals(corner[m][n])) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getShortestPath(String[][] maze, HashMap<String, List<Pair<Integer>>> portalNameToLocations) {
        Pair<Integer> aa = portalNameToLocations.get(START).get(0);
        Pair<Integer> zz = portalNameToLocations.get(END).get(0);
        Pair<Integer> current = new Pair<>(aa);

        shortestPathTo = initializeShortestPathTo(maze);
        shortestPathTo[aa.y][aa.x] = 0;

        Queue<Pair<Integer>> queue = new LinkedBlockingDeque<>();
        queue.add(current);

        while (!queue.isEmpty()) {
            Pair<Integer> node = queue.poll();
            if (!maze[node.y][node.x].equals(".")) {
                throw new RuntimeException(String.format("Not a path: %s", node));
            }
            List<Pair<Integer>> neighboursToVisit = getNeighboursAndPortalsToVisit(node, dx, dy);
            queue.addAll(neighboursToVisit);
        }
        return shortestPathTo[zz.y][zz.x];
    }

    private int getShortestRecursivePath(String[][] maze, HashMap<String, List<Pair<Integer>>> portalNameToLocations) {
        StackPair<Integer> aa = new StackPair<>(portalNameToLocations.get(START).get(0), 0);
        StackPair<Integer> zz = new StackPair<>(portalNameToLocations.get(END).get(0), 0);
        StackPair<Integer> current = new StackPair<>(aa, 0);

        HashMap<Integer, int[][]> recursionLevelToPathLength = new HashMap<>();
        recursionLevelToPathLength.put(aa.recursionDepth, initializeShortestPathTo(maze));
        recursionLevelToPathLength.get(aa.recursionDepth)[aa.y][aa.x] = 0;

        Stack<StackPair<Integer>> stack = new Stack<>();
        stack.push(current);

        while (!stack.isEmpty()) {
            StackPair<Integer> node = stack.pop();
            if (printEnabled) {
                System.out.printf("pop %s, stack: %s%n", node, stack);
                System.out.printf("recursion depth %d%n", node.recursionDepth);
            }

//            if (stack.size() > 500) {
//                throw new StackOverflowError(String.format("limit exceeded %d at recursion depth %d", stack.size(), node.recursionDepth));
//            }


            if (!maze[node.y][node.x].equals(".")) {
                throw new RuntimeException(String.format("Not a path: %s", node));
            }

            List<StackPair<Integer>> neighboursToVisit = getNeighboursAndPortalsToVisitOnRecursionLevel(node, dx, dy, recursionLevelToPathLength);
            if (printEnabled) {
                System.out.printf("adding: %s%n--------%n", neighboursToVisit);
            }
            stack.addAll(neighboursToVisit);
        }
        return recursionLevelToPathLength.get(0)[zz.y][zz.x];
    }

    private List<StackPair<Integer>> getNeighboursAndPortalsToVisitOnRecursionLevel(StackPair<Integer> node, int[] dx, int[] dy, HashMap<Integer, int[][]> recursionLevelToPathLength) {
        ArrayList<StackPair<Integer>> neighbours = new ArrayList<>();
        if (node.recursionDepth > recursionDepthLimit) return neighbours;

        int[][] pathLengths = recursionLevelToPathLength.get(node.recursionDepth);

        for (int i = 0; i < dx.length; i++) {
            StackPair<Integer> neighbour = new StackPair<>(node, node.recursionDepth);
            neighbour.x = node.x + dx[i];
            neighbour.y = node.y + dy[i];
            if (!maze[neighbour.y][neighbour.x].equals(".") || !isShorterPath(node, neighbour, pathLengths)) {
                continue;
            }

            pathLengths[neighbour.y][neighbour.x] = pathLengths[node.y][node.x] + 1;

            if (entrances.contains(neighbour)) {
                if (!recursionLevelToPathLength.containsKey(neighbour.recursionDepth + 1)) {
                    recursionLevelToPathLength.put(neighbour.recursionDepth + 1, initializeShortestPathTo(maze));
                }
                StackPair<Integer> enteredNode = new StackPair<>(getOtherPortalSide(neighbour), neighbour.recursionDepth + 1);

                int[][] upperMapLayer = recursionLevelToPathLength.get(neighbour.recursionDepth);
                int[][] lowerMapLayer = recursionLevelToPathLength.get(enteredNode.recursionDepth);

                lowerMapLayer[enteredNode.y][enteredNode.x] = upperMapLayer[neighbour.y][neighbour.x] + 1;

                if (printEnabled) {
                    System.out.printf("moving from: %s -> %s", neighbour, enteredNode);
                }
                neighbour = enteredNode;
            } else if (exits.contains(neighbour) && neighbour.recursionDepth > 0) {
                StackPair<Integer> enteredNode = new StackPair<>(getOtherPortalSide(neighbour), neighbour.recursionDepth - 1);

                int[][] lowerMapLayer = recursionLevelToPathLength.get(neighbour.recursionDepth);
                int[][] upperMapLayer = recursionLevelToPathLength.get(enteredNode.recursionDepth);

                upperMapLayer[enteredNode.y][enteredNode.x] = lowerMapLayer[neighbour.y][neighbour.x] + 1;

                if (printEnabled) {
                    System.out.printf("moving back to: %s <- %s", enteredNode, neighbour);
                }
                neighbour = enteredNode;
            }
            neighbours.add(neighbour);
        }

        return neighbours;
    }

    private List<Pair<Integer>> getNeighboursAndPortalsToVisit(Pair<Integer> node, int[] dx, int[] dy) {
        ArrayList<Pair<Integer>> neighbours = new ArrayList<>();
        for (int i = 0; i < dx.length; i++) {
            Pair<Integer> neighbour = new Pair<>(node);
            neighbour.x = node.x + dx[i];
            neighbour.y = node.y + dy[i];
            if (maze[neighbour.y][neighbour.x].equals(".") && isShorterPath(node, neighbour)) {
                neighbours.add(neighbour);
                shortestPathTo[neighbour.y][neighbour.x] = shortestPathTo[node.y][node.x] + 1;
            } else if (maze[neighbour.y][neighbour.x].matches("[B-Y]")) {
                Pair<Integer> otherPortalSide = getOtherPortalSide(node);
                if (isShorterPath(node, otherPortalSide)) {
                    neighbours.add(otherPortalSide);
                    shortestPathTo[neighbour.y][neighbour.x] = shortestPathTo[node.y][node.x] + 1;
                    shortestPathTo[otherPortalSide.y][otherPortalSide.x] = shortestPathTo[node.y][node.x] + 1;
                }
            }
        }
        return neighbours;
    }

    private Pair<Integer> getOtherPortalSide(Pair<Integer> node) {
        String portalName = positionToPortalName.get(node);
        List<Pair<Integer>> locations = portalNameToLocations.get(portalName);
        return locations.get(0).equals(node) ? locations.get(1) : locations.get(0);
    }

    private boolean isShorterPath(Pair<Integer> node, Pair<Integer> neighbour) {
        return shortestPathTo[node.y][node.x] + 1 < shortestPathTo[neighbour.y][neighbour.x];
    }

    private boolean isShorterPath(Pair<Integer> node, Pair<Integer> neighbour, int[][] shortestPathTo) {
        return shortestPathTo[node.y][node.x] + 1 < shortestPathTo[neighbour.y][neighbour.x];
    }

    private int[][] initializeShortestPathTo(String[][] maze) {
        int[][] shortestPathTo = new int[maze.length][maze[0].length];
        for (int[] row : shortestPathTo) {
            for (int i = 0; i < shortestPathTo[0].length; i++) {
                row[i] = Integer.MAX_VALUE;
            }
        }
        return shortestPathTo;
    }

    private String[][] loadMaze(List<String> lines) {
        normalize(lines);
        String[][] mazeWithLabels = new String[lines.size()][lines.get(2).length()];
        for (int i = 0; i < mazeWithLabels.length; i++) {
            for (int j = 0; j < mazeWithLabels[0].length; j++) {
                mazeWithLabels[i][j] = lines.get(i).substring(j, j + 1);
            }
        }
        return mazeWithLabels;
    }

    private void normalize(List<String> lines) {
        int maxWidth = lines.stream().map(String::length).max(Integer::compareTo).orElse(1);
        String format = String.format("%%-%ds", maxWidth + 2);
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, String.format(format, lines.get(i)));
        }
    }

    private void initializePortalNameToLocations(String[][] maze, MazeMetadata metadata) {
        portalNameToLocations = new HashMap<>();
        positionToPortalName = new HashMap<>();
        entrances = new HashSet<>();
        exits = new HashSet<>();

        String label;
        for (int y = metadata.upperOuterCorner.y; y <= metadata.lowerOuterCorner.y; y++) {
            if (maze[y][metadata.upperOuterCorner.x].equals(".")) {
                label = maze[y][metadata.upperOuterCorner.x - 2] + maze[y][metadata.upperOuterCorner.x - 1];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(metadata.upperOuterCorner.x, y);
                portalNameToLocations.get(label).add(portalLocation);
                exits.add(portalLocation);
            }

            if (maze[y][metadata.lowerOuterCorner.x].equals(".")) {
                label = maze[y][metadata.lowerOuterCorner.x + 1] + maze[y][metadata.lowerOuterCorner.x + 2];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(metadata.lowerOuterCorner.x, y);
                portalNameToLocations.get(label).add(portalLocation);
                exits.add(portalLocation);
            }
        }

        for (int x = metadata.upperOuterCorner.x; x <= metadata.lowerOuterCorner.x; x++) {
            if (maze[metadata.upperOuterCorner.y][x].equals(".")) {
                label = maze[metadata.upperOuterCorner.y - 2][x] + maze[metadata.upperOuterCorner.y - 1][x];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(x, metadata.upperOuterCorner.y);
                portalNameToLocations.get(label).add(portalLocation);
                exits.add(portalLocation);
            }

            if (maze[metadata.lowerOuterCorner.y][x].equals(".")) {
                label = maze[metadata.lowerOuterCorner.y + 1][x] + maze[metadata.lowerOuterCorner.y + 2][x];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(x, metadata.lowerOuterCorner.y);
                portalNameToLocations.get(label).add(portalLocation);
                exits.add(portalLocation);
            }
        }

        for (int y = metadata.upperInnerCorner.y; y <= metadata.lowerInnerCorner.y; y++) {
            if (maze[y][metadata.upperInnerCorner.x].equals(".")) {
                label = maze[y][metadata.upperInnerCorner.x + 1] + maze[y][metadata.upperInnerCorner.x + 2];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(metadata.upperInnerCorner.x, y);
                portalNameToLocations.get(label).add(portalLocation);
                entrances.add(portalLocation);
            }

            if (maze[y][metadata.lowerInnerCorner.x].equals(".")) {
                label = maze[y][metadata.lowerInnerCorner.x - 2] + maze[y][metadata.lowerInnerCorner.x - 1];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(metadata.lowerInnerCorner.x, y);
                portalNameToLocations.get(label).add(portalLocation);
                entrances.add(portalLocation);
            }
        }

        for (int x = metadata.upperInnerCorner.x; x <= metadata.lowerInnerCorner.x; x++) {
            if (maze[metadata.upperInnerCorner.y][x].equals(".")) {
                label = maze[metadata.upperInnerCorner.y + 1][x] + maze[metadata.upperInnerCorner.y + 2][x];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(x, metadata.upperInnerCorner.y);
                portalNameToLocations.get(label).add(portalLocation);
                entrances.add(portalLocation);
            }

            if (maze[metadata.lowerInnerCorner.y][x].equals(".")) {
                label = maze[metadata.lowerInnerCorner.y - 2][x] + maze[metadata.lowerInnerCorner.y - 1][x];
                if (!portalNameToLocations.containsKey(label)) {
                    portalNameToLocations.put(label, new ArrayList<>());
                }
                Pair<Integer> portalLocation = new Pair<>(x, metadata.lowerInnerCorner.y);
                portalNameToLocations.get(label).add(portalLocation);
                entrances.add(portalLocation);
            }
        }

        portalNameToLocations.forEach((key, positions) -> positions.forEach(position -> positionToPortalName.put(position, key)));
        exits.removeIf(entrance -> portalNameToLocations.get(START).contains(entrance)
                || portalNameToLocations.get(END).contains(entrance));
    }

    static class MazeMetadata {
        Pair<Integer> upperOuterCorner;
        Pair<Integer> lowerOuterCorner;
        Pair<Integer> upperInnerCorner;
        Pair<Integer> lowerInnerCorner;

        public MazeMetadata() {
            this.upperOuterCorner = new Pair<>(0, 0);
            this.lowerOuterCorner = new Pair<>(0, 0);
            this.upperInnerCorner = new Pair<>(0, 0);
            this.lowerInnerCorner = new Pair<>(0, 0);
        }
    }

    class MazePreview {
        @Override
        public String toString() {
            if (maze == null) return "N/A";
            StringBuilder mazeStr = new StringBuilder();
            for (String[] strings : maze) {
                for (int j = 0; j < maze[0].length; j++) {
                    mazeStr.append(strings[j]);
                }
                mazeStr.append("\n");
            }
            return mazeStr.toString();
        }
    }

    static class StackPair<T> extends Pair<T> {
        int recursionDepth;

        public StackPair(Pair<T> pair, int recursionDepth) {
            super(pair);
            this.recursionDepth = recursionDepth;
        }

        @Override
        public String toString() {
            return String.format("{%s, %s, D=%d}", x, y, recursionDepth);
        }
    }
}
