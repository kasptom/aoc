package year2019.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Aoc2019Utils {
    /* parsed instruction indexes */
    public static final int MAX_INSTRUCTION_SIZE = 5;
    public static final int IDX_OPCODE_A = 0;
    public static final int IDX_OPCODE_B = 1;
    public static final int IDX_MODE1 = 2;
    public static final int IDX_MODE2 = 3;
    public static final int IDX_MODE3 = 4;

    /* instruction opcodes */
    public static final int INSTR_STOP = 99;
    public static final int INSTR_ADD = 1;
    public static final int INSTR_MUL = 2;
    public static final int INSTR_INPUT = 3;
    public static final int INSTR_OUTPUT = 4;
    /* part 2 instructions */
    public static final int INSTR_JMP_TRUE = 5;
    public static final int INSTR_JMP_FALSE = 6;
    public static final int INSTR_LT = 7;
    public static final int INSTR_EQ = 8;
    /* relative mode */
    public static final int INSTR_RBASE = 9;

    /* parameter modes */
    public static final int MODE_POSITION = 0;
    public static final int MODE_IMMEDIATE = 1;
    public static final int MODE_RELATIVE = 2;

    public static long[] loadProgram(List<String> lines) {
        List<Long> code = Arrays.stream(lines.get(0).split(","))
                .map(Long::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));

        long[] parsedCode = new long[code.size()];
        for (int i = 0; i < code.size(); i++) {
            parsedCode[i] = code.get(i);
        }
        return parsedCode;
    }

    public static TreeSet<Integer> createSieve(int maxValue) {
        List<Integer> sieve = IntStream.range(2, maxValue + 1).boxed().collect(Collectors.toList());
        sieve = sieve.stream().filter(number -> number == 2 || number % 2 != 0).collect(Collectors.toList());

        for (int i = 3; i * i < maxValue; i += 2) {
            for (int j = 2 * i; j < maxValue; j += i) {
                sieve.remove(Integer.valueOf(j));
            }
        }
//        sieve.forEach(el -> System.out.format("%d ", el));
//        System.out.println();
        return new TreeSet<>(sieve);
    }

    public static long[] copyToLargerMemory(long[] parsedCode, int size) {
        long[] largerMemory = new long[size];
        System.arraycopy(parsedCode, 0, largerMemory, 0, parsedCode.length);
        parsedCode = largerMemory;
        return parsedCode;
    }
}
