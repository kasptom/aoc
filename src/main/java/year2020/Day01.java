package year2020;

import aoc.IAocTask;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Day01 implements IAocTask {
    private int SUM_TO_FIND = 2020;

    @Override
    public String getFileName() {
        return "aoc2020/input_01.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        var complements = new HashSet<Integer>();
        var expenses = getExpenses(lines);
        for (var expense : expenses) {
            if (complements.contains(expense)) {
                System.out.println(expense * (SUM_TO_FIND - expense));
                break;
            }
            complements.add(2020 - expense);
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        var expenses = getExpenses(lines);
        for (int i = 0; i < expenses.size(); i++) {
            for (int j = i + 1; j < expenses.size(); j++) {
                for (int k = j + 1; k < expenses.size(); k++) {
                    var a = expenses.get(i);
                    var b = expenses.get(j);
                    var c = expenses.get(k);
                    if (a + b + c == SUM_TO_FIND) {
                        System.out.println(a * b * c);
                        break;
                    }
                }
            }
        }
    }

    private List<Integer> getExpenses(List<String> lines) {
        return lines.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
