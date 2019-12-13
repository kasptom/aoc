package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.util.List;

public class Day09 implements IAocTask {
    @Override
    public String getFileName() {
//        return "aoc2019/input_09_small.txt";
//        return "aoc2019/input_09_quine.txt";
        return "aoc2019/input_09.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        long input = 1L;
        solve(lines, input);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        long input = 2L;
        solve(lines, input);
    }

    private void solve(List<String> lines, long input) {
        long[] parsedCode = Aoc2019Utils.loadProgram(lines);
        parsedCode = Aoc2019Utils.copyToLargerMemory(parsedCode, 6000);

        final long[] output = new long[2];
        Day05 intComp = new Day05();
        intComp.setIoListeners(() -> {
            System.out.printf("IN: %d%n", input);
            return input;
        }, (out, iPointer) -> {
            output[0] = out;
            System.out.printf("OUT: %d IP: %d%n", out, iPointer);
            return false;
        }, () -> {
        });
        intComp.runProgram(parsedCode, new long[2]);
        System.out.println(output[0]);
    }
}
