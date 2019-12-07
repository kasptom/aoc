package year2019.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    /* parameter modes */
    public static final int MODE_POSITION = 0;
    public static final int MODE_IMMEDIATE = 1;

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
}
