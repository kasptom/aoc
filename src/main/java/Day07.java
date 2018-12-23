import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day07 implements IAocTask {

    int maxTaskId = 0;

    @Override
    public String getFileName() {
        return "input_07.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int[][] graph = createGraph(lines);
        printGraph(graph);

        runKahn(graph);
    }

    private void runKahn(int[][] graph) {
        List<Integer> processingOrder = new ArrayList<>();
        HashSet<Integer> independent = new HashSet<>();

        for (int row = 0; row < maxTaskId; row++) {
            if (isIndependent(graph[row])) {
                independent.add(row);
            }
        }

        independent.forEach(taskId -> System.out.printf("%c", taskId + 65));

        while (!independent.isEmpty()) {
            int independentId = independent.stream().min(Integer::compareTo).get();
            independent.remove(independentId);
            processingOrder.add(independentId);

            for (int row = 0; row <= maxTaskId; row++) {

                graph[row][independentId] = 0;
                if (isIndependent(graph[row]) && !processingOrder.contains(row)) {
                    independent.add(row);
                }
            }
//            printGraph(graph);
        }

        System.out.println();
        processingOrder.forEach(independentId -> System.out.printf("%c", independentId + 'A'));
    }

    private boolean isIndependent(int[] array) {
        return Arrays.stream(array).noneMatch(dep -> dep == 1);
    }

    private int[][] createGraph(List<String> lines) {
        // Step C must be finished before step A can begin.
        String STEP_PAIR_REGEX = "Step ([A-Z]) must be finished before step ([A-Z]) can begin";
        Pattern pattern = Pattern.compile(STEP_PAIR_REGEX);

        int[][] graph = new int[26][26];

        for (String line : lines) {

            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String prevName = matcher.group(1);
                String nextName = matcher.group(2);

                int nextId = (int) nextName.charAt(0) - 'A';
                int prevId = (int) prevName.charAt(0) - 'A';

                maxTaskId = maxTaskId < prevId ? prevId : maxTaskId;
                maxTaskId = maxTaskId < nextId ? nextId : maxTaskId;

                graph[nextId][prevId]++;
            }
        }

        return graph;
    }

    private void printGraph(int[][] graph) {
        System.out.println(" A B C D E F G H I J K L M N O P Q R S T U W X Y Z");
        for (int i = 0; i <= maxTaskId; i++) {
            System.out.print((char) ('A' + i));
            for (int j = 0; j <= maxTaskId; j++) {
                if (graph[i][j] == 1) {
                    System.out.print("Z ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
