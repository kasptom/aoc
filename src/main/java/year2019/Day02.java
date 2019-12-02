package year2019;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day02 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2019/input_02.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Integer> code = Arrays.stream(lines.get(0).split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));

        code.set(1, 12);
        code.set(2, 2);

        int[] parsedCode = new int[code.size()];
        for (int i = 0; i < code.size(); i++) {
            parsedCode[i] = code.get(i);
        }

        runProgram(parsedCode);
        System.out.println(parsedCode[0]);
    }

    private void runProgram(int[] parsedCode) {
        int i = 0;
        for (; i < parsedCode.length; ) {
            if (parsedCode[i] == 99) {
                return;
            }
            if (parsedCode[i] == 1) {
                addNumbers(i, parsedCode);
                i += 4;
            } else if (parsedCode[i] == 2) {
                multiplyNumbers(i, parsedCode);
                i += 4;
            }
        }
    }

    private void addNumbers(int i, int[] parsedCode) {
        parsedCode[parsedCode[i + 3]] = parsedCode[parsedCode[i + 1]] + parsedCode[parsedCode[i + 2]];
    }

    private void multiplyNumbers(int i, int[] parsedCode) {
        parsedCode[parsedCode[i + 3]] = parsedCode[parsedCode[i + 1]] * parsedCode[parsedCode[i + 2]];
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
