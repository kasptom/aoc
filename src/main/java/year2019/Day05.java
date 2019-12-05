package year2019;

import aoc.IAocTask;

import java.util.List;

import static year2019.utils.Aoc2019Utils.*;

public class Day05 implements IAocTask {

    private int input;

    @Override
    public String getFileName() {
        return "aoc2019/input_05.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int[] parsedCode = loadProgram(lines);
//        int[] test = {3, 0, 4, 0, 99};
//        int[] test = {1002, 4, 3, 4, 33};
        runProgram(parsedCode);
    }

    private void runProgram(int[] parsedCode) {
        int i = 0;
        for (; i < parsedCode.length; ) {
            i = runInstructions(parsedCode, i);
        }
    }

    // https://adventofcode.com/2019/day/5
    private int runInstructions(int[] parsedCode, int i) {
        int[] instruction = getInstruction(parsedCode[i]);
        if (instruction[IDX_OPCODE_A] + 10 * instruction[IDX_OPCODE_B] == INSTR_STOP) {
            return parsedCode.length;
        }

        if (instruction[IDX_OPCODE_A] == INSTR_ADD) {
            parsedCode[parsedCode[i + 3]] = addNumbers(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_MUL) {
            parsedCode[parsedCode[i + 3]] = multiplyNumbers(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_INPUT) {
            input = parsedCode[parsedCode[i + 1]];
            i += 2;
        } else if (instruction[IDX_OPCODE_A] == INSTR_OUTPUT) {
            parsedCode[parsedCode[i + 1]] = input;
            i += 2;
        }
        return i;
    }

    private int addNumbers(int i, int[] parsedCode, int[] instruction) {
        int a = instruction[IDX_MODE1] == MODE_IMMEDIATE
                ? parsedCode[i + 1]
                : parsedCode[parsedCode[i + 1]];
        int b = instruction[IDX_MODE1] == MODE_IMMEDIATE
                ? parsedCode[i + 2]
                : parsedCode[parsedCode[i + 2]];
        return a + b;
    }

    private int multiplyNumbers(int i, int[] parsedCode, int[] instruction) {
        int a = instruction[IDX_MODE1] == MODE_IMMEDIATE
                ? parsedCode[i + 1]
                : parsedCode[parsedCode[i + 1]];
        int b = instruction[IDX_MODE2] == MODE_IMMEDIATE
                ? parsedCode[i + 2]
                : parsedCode[parsedCode[i + 2]];
        return a * b;
    }

    /**
     * sets the instruction modes and opcodes
     *
     * @param code instruction pointer
     * @return parsed instruction
     */
    private int[] getInstruction(int code) {
        int[] instruction = new int[MAX_INSTRUCTION_SIZE];
        instruction[IDX_OPCODE_A] = code % 10;
        code /= 10;
        instruction[IDX_OPCODE_B] = code % 10;
        code /= 10;
        instruction[IDX_MODE1] = code % 10;
        code /= 10;
        instruction[IDX_MODE2] = code % 10;
        code /= 10;
        instruction[IDX_MODE3] = code % 10;
        return instruction;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
