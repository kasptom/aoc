package year2019.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Aoc2019Utils {
    public static int[] loadProgram(List<String> lines) {
        List<Integer> code = Arrays.stream(lines.get(0).split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));

        int[] parsedCode = new int[code.size()];
        for (int i = 0; i < code.size(); i++) {
            parsedCode[i] = code.get(i);
        }
        return parsedCode;
    }

    public static int runBasicInstructions(int[] parsedCode, int i) {
        if (parsedCode[i] == 99) {
            i = parsedCode.length;
        } else if (parsedCode[i] == 1) {
            addNumbers(i, parsedCode);
            i += 4;
        } else if (parsedCode[i] == 2) {
            multiplyNumbers(i, parsedCode);
            i += 4;
        }
        return i;
    }

    private static void addNumbers(int i, int[] parsedCode) {
        parsedCode[parsedCode[i + 3]] = parsedCode[parsedCode[i + 1]] + parsedCode[parsedCode[i + 2]];
    }

    private static void multiplyNumbers(int i, int[] parsedCode) {
        parsedCode[parsedCode[i + 3]] = parsedCode[parsedCode[i + 1]] * parsedCode[parsedCode[i + 2]];
    }
}
