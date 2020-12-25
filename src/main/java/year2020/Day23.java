package year2020;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day23 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_23.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int moves = 100;
        List<Integer> cupsList = Arrays.stream(lines.get(0).split("")).map(Integer::valueOf).collect(Collectors.toList());
        int maxValue = cupsList.stream().max(Integer::compareTo).orElse(-1);
        HashMap<Integer, Integer> cupToNextIdx = new HashMap<>();
        int[] cups = new int[cupsList.size()];
        solve(moves, cupsList, maxValue, cups, cupToNextIdx);
        printLabelsAfterCupOne(cups, cupToNextIdx);
    }

    private void solve(int moves, List<Integer> cupsList, int maxValue, int[] cups, HashMap<Integer, Integer> cupToNextIdx) {
        HashMap<Integer, Integer> cupToPrevIdx = new HashMap<>();
        HashMap<Integer, Integer> cupToIdx = new HashMap<>();
        for (int i = 0; i < cupsList.size(); i++) {
            cups[i] = cupsList.get(i);
            cupToPrevIdx.put(cups[i], i - 1 < 0 ? cupsList.size() - 1 : i - 1);
            cupToNextIdx.put(cups[i], (i + 1) % cupsList.size());
            cupToIdx.put(cups[i], i);
        }
        int currentIdx = 0;
        for (int i = 0; i < moves; i++) {
            currentIdx = move(currentIdx, cups, cupToNextIdx, cupToPrevIdx, cupToIdx, maxValue);
        }
    }

    private void printLabelsAfterCupOne(int[] cups, HashMap<Integer, Integer> cupToNextIdx) {
        int next = 1;
        for (int i = 0; i < cupToNextIdx.size() - 1; i++) {
            next = cups[cupToNextIdx.get(next)];
            System.out.print(next);
        }
        System.out.println();
    }

    /**
     * Each move, the crab does the following actions:
     * <p>
     * - The crab picks up the three cups that are immediately clockwise of the current cup. They are removed from
     * the circle; cup spacing is adjusted as necessary to maintain the circle.
     * - The crab selects a destination cup: the cup with a label equal to the current cup's label minus one.
     * If this would select one of the cups that was just picked up, the crab will keep subtracting one until it
     * finds a cup that wasn't just picked up. If at any point in this process the value goes below the lowest value
     * on any cup's label, it wraps around to the highest value on any cup's label instead.
     * - The crab places the cups it just picked up so that they are immediately clockwise of the destination cup.
     * They keep the same order as when they were picked up.
     * - The crab selects a new current cup: the cup which is immediately clockwise of the current cup.
     */
    private int move(int currentIdx, int[] cups, HashMap<Integer, Integer> cupToNextIdx, HashMap<Integer, Integer> cupToPrevIdx, HashMap<Integer, Integer> cupToIdx, int maxValue) {
        int pickedIdx = currentIdx;
        // 1
        List<Integer> picked = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pickedIdx = cupToNextIdx.get(cups[pickedIdx]);
            picked.add(cups[pickedIdx]);
        }
        // removing picked from the circle (currentNext is the next of the next)
        int firstPickedIdx = cupToNextIdx.get(cups[currentIdx]);

        cupToNextIdx.put(cups[currentIdx], cupToNextIdx.get(cups[pickedIdx])); // current.next = picked#3.next
        cupToPrevIdx.put(cups[cupToNextIdx.get(cups[pickedIdx])], currentIdx); // picked#3.next.prev = current

        int destinationCup = (cups[currentIdx] - 1);
        destinationCup = destinationCup < 1 ? maxValue : destinationCup;
        while (picked.contains(destinationCup)) {
            destinationCup--;
            destinationCup = destinationCup < 1 ? maxValue : destinationCup;
        }
        // The crab places the cups it just picked up so that they are immediately clockwise of the destination cup.
        cupToNextIdx.put(cups[pickedIdx], cupToNextIdx.get(destinationCup));
        cupToPrevIdx.put(cups[cupToNextIdx.get(destinationCup)], cupToNextIdx.get(cups[currentIdx])); // todo prev idx update

        cupToNextIdx.put(destinationCup, firstPickedIdx);
        cupToPrevIdx.put(cups[firstPickedIdx], cupToIdx.get(destinationCup));

        return cupToNextIdx.get(cups[currentIdx]);
    }


    @Override
    public void solvePartTwo(List<String> lines) {
        int moves = 10_000_000;
        List<Integer> cupsList = Arrays.stream(lines.get(0).split("")).map(Integer::valueOf).collect(Collectors.toList());
        int maxValue = cupsList.stream().max(Integer::compareTo).orElse(-1);
        cupsList.addAll(IntStream.range(maxValue + 1, 1_000_001).boxed().collect(Collectors.toList()));
        maxValue = 1_000_000;

        HashMap<Integer, Integer> cupToNextIdx = new HashMap<>();
        int[] cups = new int[cupsList.size()];
        solve(moves, cupsList, maxValue, cups, cupToNextIdx);
        long product = getProductOfNextTwoAfterOne(cups, cupToNextIdx);
        System.out.println(product);
    }

    private long getProductOfNextTwoAfterOne(int[] cups, HashMap<Integer, Integer> cupToNextIdx) {
        long first = cups[cupToNextIdx.get(1)];
        long second = cups[cupToNextIdx.get((int)first)];
        System.out.format("%d * %d = %d\n", first, second, first * second);
        return first * second;
    }
}
