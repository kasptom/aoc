package year2019;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day02 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2019/input_02.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int[] parsedCode = loadProgram(lines);
        parsedCode[1] = 12;
        parsedCode[2] = 2;
        runProgram(parsedCode);
        System.out.println(parsedCode[0]);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int[] parsedCode = loadProgram(lines);
        int[] copied = runProgramV2(parsedCode);
        System.out.format("%d", 100 * copied[1] + copied[2]);
    }

    private int[] loadProgram(List<String> lines) {
        List<Integer> code = Arrays.stream(lines.get(0).split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));

        int[] parsedCode = new int[code.size()];
        for (int i = 0; i < code.size(); i++) {
            parsedCode[i] = code.get(i);
        }
        return parsedCode;
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

    private int[] runProgramV2(int[] parsedCode) {

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                int[] copied = new int[parsedCode.length];
                System.arraycopy(parsedCode, 0, copied, 0, parsedCode.length);
                copied[1] = i;
                copied[2] = j;
                runProgram(copied);
                if (copied[0] == 19690720) {
                    System.out.println("yes");
                    return copied;
                }
            }
        }
        return parsedCode;
    }

    private void addNumbers(int i, int[] parsedCode) {
        parsedCode[parsedCode[i + 3]] = parsedCode[parsedCode[i + 1]] + parsedCode[parsedCode[i + 2]];
    }

    private void multiplyNumbers(int i, int[] parsedCode) {
        parsedCode[parsedCode[i + 3]] = parsedCode[parsedCode[i + 1]] * parsedCode[parsedCode[i + 2]];
    }
}
