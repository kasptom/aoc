package year2020;

import aoc.IAocTask;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Day01 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_01.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        HashSet<Integer> complements = new HashSet<>();
        List<Integer> expenses = lines.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        for (var expense : expenses) {
            if (complements.contains(expense)) {
                System.out.println(expense * (2020 - expense));
                break;
            }
            complements.add(2020 - expense);
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
