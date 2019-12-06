package year2019;

import aoc.IAocTask;

import java.util.*;

public class Day06 implements IAocTask {
    //
    HashMap<String, String> childToParent = new HashMap<>();
    HashMap<String, HashSet<String>> parentToChildren = new HashMap<>();
    HashMap<String, Integer> nodeDistanceToRoot = new HashMap<>();

    @Override
    public String getFileName() {
//        return "aoc2019/input_06_small2.txt";
        return "aoc2019/input_06.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        for (String line : lines) {
            String[] parentChild = line.split("\\)");
            String parent = parentChild[0];
            String child = parentChild[1];
            childToParent.put(child, parent);
            if (!parentToChildren.containsKey(parent)) {
                parentToChildren.put(parent, new HashSet<>());
            }
            parentToChildren.get(parent).add(child);

            nodeDistanceToRoot.put(parent, 0);
            nodeDistanceToRoot.put(child, 0);
        }

        String root = findRoot(nodeDistanceToRoot.keySet().iterator().next());
        System.out.printf("Root: %s\n", root);
        updateDistances(root, 0);

        System.out.println(nodeDistanceToRoot.values().stream().reduce(Integer::sum).orElse(0));
    }

    private void updateDistances(String root, int distance) {
        nodeDistanceToRoot.put(root, distance);
        distance++;
        if (!parentToChildren.containsKey(root)) {
            return;
        }
        HashSet<String> children = parentToChildren.get(root);
        for (String child: children) {
            nodeDistanceToRoot.put(child, distance);
            updateDistances(child, distance);
        }
    }

    private String findRoot(String node) {
        if (!childToParent.containsKey(node)) {
            return node;
        }
        return findRoot(childToParent.get(node));
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        String santa = "SAN";
        String you = "YOU";
        List<String> santaRouteToRoot = new ArrayList<>();
        List<String> myRouteToRoot = new ArrayList<>();
        getRouteToRoot(santa, santaRouteToRoot);
        getRouteToRoot(you, myRouteToRoot);
        findClosestCommonSubOrbit(santaRouteToRoot, myRouteToRoot);
    }

    private void findClosestCommonSubOrbit(List<String> santaRouteToRoot, List<String> myRouteToRoot) {
        String closestCommonNode = "???";
        String foundNode;
        Collections.reverse(santaRouteToRoot);
        Collections.reverse(myRouteToRoot);

        for (String node: santaRouteToRoot) {
            foundNode = myRouteToRoot.stream().filter(node::equals).findFirst().orElse(null);
            if (foundNode != null) {
                closestCommonNode = foundNode;
            }
        }
        int santaDistToClosest = santaRouteToRoot.size() - santaRouteToRoot.lastIndexOf(closestCommonNode);
        int myDistToClosest = myRouteToRoot.size() - myRouteToRoot.lastIndexOf(closestCommonNode);

        System.out.println(closestCommonNode);
        System.out.println(santaDistToClosest + myDistToClosest - 2);
    }

    private void getRouteToRoot(String node, List<String> path) {
        if (!childToParent.containsKey(node)) {
            return;
        }
        path.add(childToParent.get(node));
        getRouteToRoot(childToParent.get(node), path);
    }
}
