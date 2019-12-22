package year2018;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day07 implements IAocTask {

    private static final int NUMBER_OF_ELVES = 5;
    private static final int BASE_TIME_SECONDS = 60;
    private int maxTaskId = 0;
    private int[] taskTimeLeft = createTaskTimes();

    private List<Elf> elves = new ArrayList<>();
    private final HashSet<Integer> currentlyProcessed = new HashSet<>();

    private int[] createTaskTimes() {
        int[] times = new int[26];
        for (int i = 0; i < 26; i++) {
            times[i] = i + 1 + BASE_TIME_SECONDS;
        }
        return times;
    }

    @Override
    public String getFileName() {
        return "aoc2018/input_07.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int[][] graph = createGraph(lines);
        printGraph(graph);

        runKahn(graph);
    }

    private void runKahn(int[][] graph) {
        List<Integer> processingOrder = new ArrayList<>();
        HashSet<Integer> independent = createIndependentTaskSet(graph);

        while (!independent.isEmpty()) {
            int independentId = getNextIndependentId(independent);
            processingOrder.add(independentId);
            updateGraph(graph, processingOrder, independent, independentId);
//            printGraph(graph);
        }

        processingOrder.forEach(independentId -> System.out.printf("%c", independentId + 'A'));
        System.out.println();
    }

    private Integer getNextIndependentId(HashSet<Integer> independent) {
        Integer independentId;
        independentId = independent.stream().min(Integer::compareTo).orElse(-1);
        independent.remove(independentId);
        return independentId;
    }

    private HashSet<Integer> createIndependentTaskSet(int[][] graph) {
        HashSet<Integer> independent = new HashSet<>();

        for (int row = 0; row < maxTaskId; row++) {
            if (isIndependent(graph[row])) {
                independent.add(row);
            }
        }

        independent.forEach(taskId -> System.out.printf("%c", taskId + 65));
        System.out.println();

        return independent;
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
        for (int i = 1; i <= NUMBER_OF_ELVES; i++) {
            elves.add(new Elf(i));
        }

        int[][] graph = createGraph(lines);
//        printGraph(graph);

        runKahnWithElves(graph, elves);
    }

    private void runKahnWithElves(int[][] graph, List<Elf> elves) {
        List<Integer> processingOrder = new ArrayList<>();
        HashSet<Integer> independent = createIndependentTaskSet(graph);

        int second = -1;
        while (!independent.isEmpty() || elves.stream().anyMatch(elf -> !elf.isAvailable())) {
            second++;
            elves.forEach(Elf::onClockTick);

            elves.stream()
                    .filter(Elf::isAvailable)
                    .forEach(elf -> {
                        if (elf.currentTaskId != -1) {
                            processingOrder.add(elf.currentTaskId);
                            updateGraph(graph, processingOrder, independent, elf.finishTask());
                        }
                    });
            elves.stream()
                    .filter(Elf::isAvailable)
                    .forEach(elf -> {
                        int taskId = getNextIndependentId(independent);

                        if (taskId != -1) {
                            elf.assignTask(taskId);
                        }
                    });

            printWorkStatus(elves, second);
//            printGraph(graph);
        }

        System.out.println();
        processingOrder.forEach(independentId -> System.out.printf("%c", independentId + 'A'));
    }

    private void printWorkStatus(List<Elf> elves, int second) {
        System.out.printf("%3d ", second);
        elves.forEach(elf -> {
            String currentTask = elf.currentTaskId != -1 ? "" + (char) (elf.currentTaskId + 'A') : ".";
            System.out.print(currentTask + "\t");
        });
        System.out.println();
    }

    private void updateGraph(int[][] graph, List<Integer> processingOrder, HashSet<Integer> independent, int independentId) {
        for (int row = 0; row <= maxTaskId; row++) {
            graph[row][independentId] = 0;
            if (isIndependent(graph[row]) && !processingOrder.contains(row) && !currentlyProcessed.contains(row)) {
                independent.add(row);
            }
        }
    }

    class Elf {
        int elfId;
        int timeLeft;
        int currentTaskId = -1;

        Elf(int elfId) {
            this(elfId, 0);
        }

        Elf(int elfId, int timeLeft) {
            this.elfId = elfId;
            this.timeLeft = timeLeft;
        }

        boolean isAvailable() {
            return timeLeft == 0;
        }

        void assignTask(int taskId) {
            this.currentTaskId = taskId;
            currentlyProcessed.add(taskId);
            this.timeLeft += taskTimeLeft[taskId];
        }

        int finishTask() {
            currentlyProcessed.remove(currentTaskId);
            timeLeft = 0;
            int currentTaskId = this.currentTaskId;
            this.currentTaskId = -1;
            return currentTaskId;
        }

        private void onClockTick() {
            this.timeLeft--;
            if (timeLeft < 0) {
                timeLeft = 0;
            }
        }
    }
}
