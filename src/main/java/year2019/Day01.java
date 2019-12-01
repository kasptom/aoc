package year2019;

import aoc.IAocTask;

import java.util.List;

public class Day01 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2019/input_01.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        Integer sum = lines.stream()
                .map(Integer::valueOf)
                .map(mass -> mass / 3 - 2)
                .reduce(Integer::sum)
                .orElse(-1);
        System.out.println(sum);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        Integer sum = lines.stream()
                .map(Integer::valueOf)
                .map(this::getRecursiveSum)
                .reduce(Integer::sum)
                .orElse(-1);
        System.out.println(sum);

    }

    private int getRecursiveSum(Integer mass) {
        int sum = 0;
        mass = mass / 3 - 2;
        while (mass > 0) {
            sum += mass;
            mass = mass / 3 - 2;
        }
        return sum;
    }
}
