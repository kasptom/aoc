package year2019;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Day04 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc/year2019/input_04.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Integer> range = Arrays.stream(lines.get(0).split("-"))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
        Integer lower = range.get(0);
        Integer upper = range.get(1);

        int count = countNumbersMeetingCriteria(lower, upper);
    }

    private int countNumbersMeetingCriteria(Integer lower, Integer upper) {
        int digitsCount = 6;
        int currentDigitIdx = 0;

        return 0;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
