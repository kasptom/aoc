package year2019;

import aoc.IAocTask;

import java.util.List;

import static year2019.utils.Aoc2019Utils.*;

public class Day02 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2019/input_02.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        long[] parsedCode = loadProgram(lines);
        parsedCode[1] = 12;
        parsedCode[2] = 2;
        runProgram(parsedCode);
        System.out.println(parsedCode[0]);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        long[] parsedCode = loadProgram(lines);
        long[] copied = runProgramV2(parsedCode);
        System.out.format("%d", 100 * copied[1] + copied[2]);
    }

    private void runProgram(long[] parsedCode) {
        int i = 0;
        for (; i < parsedCode.length; ) {
            i = runBasicInstructions(parsedCode, i);
        }
    }

    private long[] runProgramV2(long[] parsedCode) {

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                long[] copied = new long[parsedCode.length];
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

    public static int runBasicInstructions(long[] parsedCode, int i) {
        if (parsedCode[i] == INSTR_STOP) {
            i = parsedCode.length;
        } else if (parsedCode[i] == INSTR_ADD) {
            addNumbers(i, parsedCode);
            i += 4;
        } else if (parsedCode[i] == INSTR_MUL) {
            multiplyNumbers(i, parsedCode);
            i += 4;
        }
        return i;
    }

    public static void addNumbers(int i, long[] parsedCode) {
        parsedCode[(int) parsedCode[i + 3]] = parsedCode[(int) parsedCode[i + 1]] + parsedCode[(int) parsedCode[i + 2]];
    }

    public static void multiplyNumbers(int i, long[] parsedCode) {
        parsedCode[(int) parsedCode[i + 3]] = parsedCode[(int) parsedCode[i + 1]] * parsedCode[(int) parsedCode[i + 2]];
    }
}
