package year2019;

import aoc.IAocTask;

import java.util.List;

import static year2019.utils.Aoc2019Utils.*;

public class Day05 implements IAocTask {

    private int input;
    private int parameterMode;

    @Override
    public String getFileName() {
        return "aoc2019/input_05.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int[] parsedCode = loadProgram(lines);
        //int[] test = {3, 0, 4, 0, 99};
        runProgram(parsedCode);
    }

    private void runProgram(int[] parsedCode) {
        int i = 0;
        for (; i < parsedCode.length; ) {
            i = runBasicInstructions(parsedCode, i);
            i = runAdditionalInstructions(parsedCode, i);
        }
    }

    // https://adventofcode.com/2019/day/5
    private int runAdditionalInstructions(int[] parsedCode, int i) {
        if (i == parsedCode.length) {
            return i;
        } else if (parsedCode[i] == INSTR_INPUT){
            input = parsedCode[parsedCode[i + 1]];
            i += 2;
        } else if (parsedCode[i] == INSTR_OUTPUT) {
            parsedCode[parsedCode[i + 1]] = input;
            i += 2;
        }
        return i;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
