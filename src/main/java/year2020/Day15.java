package year2020;

import aoc.IAocTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Day15 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_15.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Integer> numbers = Arrays.stream(lines.get(0).split(",")).map(Integer::valueOf).collect(Collectors.toList());
        int spoken2000th = getNumberSpokenAt(numbers, 2020);
        System.out.println(spoken2000th);
    }

    private int getNumberSpokenAt(List<Integer> numbers, int turns) {
        Integer lastSpoken = -1;
        HashMap<Integer, Integer> numberToPrevTurnSpoken = new HashMap<>();
        HashMap<Integer, Integer> numberToTurnSpoken = new HashMap<>();
        numbers.forEach(num -> numberToTurnSpoken.put(num, numbers.indexOf(num) + 1));
        lastSpoken = numbers.get(numbers.size() - 1);
        for (int turn = numbers.size() + 1; turn <= turns; turn++) {
            int nowSpoken;
            if (!numberToPrevTurnSpoken.containsKey(lastSpoken)) {
                nowSpoken = 0;
            } else {
                var prevPrevTurn = numberToPrevTurnSpoken.get(lastSpoken);
                var prevTurn = numberToTurnSpoken.get(lastSpoken);
                nowSpoken = prevTurn - prevPrevTurn;
            }
            if (numberToTurnSpoken.containsKey(nowSpoken)) {
                numberToPrevTurnSpoken.put(nowSpoken, numberToTurnSpoken.get(nowSpoken));
            }
            numberToTurnSpoken.put(nowSpoken, turn);
            lastSpoken = nowSpoken;
        }
        return lastSpoken;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Integer> numbers = Arrays.stream(lines.get(0).split(",")).map(Integer::valueOf).collect(Collectors.toList());
        int spoken2000th = getNumberSpokenAt(numbers, 30000000);
        System.out.println(spoken2000th);
    }
}
