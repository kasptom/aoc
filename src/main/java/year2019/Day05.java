package year2019;

import aoc.IAocTask;

import java.util.Arrays;
import java.util.List;

import static year2019.utils.Aoc2019Utils.*;

public class Day05 implements IAocTask {

    private int inputOutput;
    private int checkPoint;
    private int prevInstructionIdx;
    private int[] parsedCode;
    private int[] backup;
    private boolean finished= false;

    @Override
    public String getFileName() {
        return "aoc2019/input_05.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        parsedCode = loadProgram(lines);
        backup = new int[parsedCode.length];
        System.arraycopy(parsedCode, 0, backup, 0, parsedCode.length);
//        int[] test = {3, 0, 4, 0, 99};
//        int[] test = {1002, 4, 3, 4, 33};
        runProgram(0);
    }

    private int modifyParameters(int operationIdx, int instructionPointer, int[] permutation) {
        int[] instruction = getInstruction(parsedCode[instructionPointer]);
        instruction[IDX_MODE1] = permutation[2 * operationIdx];
        instruction[IDX_MODE2] = permutation[2 * operationIdx + 1];
        int newOpCode = 0;
        for (int j = 3; j >= 0; j--) {
            newOpCode *= 10;
            newOpCode += instruction[j];
        }
        return newOpCode;
    }

    private void runProgram(int i) {
        if (finished) {
            return;
        }
        inputOutput = i > 0 ? 0 : 1;
//        int executionCounter = 0;
        for (; i < parsedCode.length; /* && executionCounter < 1e9; */ ) {
            i = runInstructions(i);
//            executionCounter++;
        }
    }

    // https://adventofcode.com/2019/day/5
    private int runInstructions(int i) {
        int[] instruction = getInstruction(parsedCode[i]);
        if (instruction[IDX_OPCODE_A] + 10 * instruction[IDX_OPCODE_B] == INSTR_STOP) {
            System.out.printf("TESTS PASSED!!! OUTPUT: %d\n", inputOutput);
            finished = true;
            return parsedCode.length;
        }

        if (instruction[IDX_OPCODE_A] == INSTR_ADD) {
            prevInstructionIdx = i;
            parsedCode[parsedCode[i + 3]] = addNumbers(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_MUL) {
            prevInstructionIdx = i;
            parsedCode[parsedCode[i + 3]] = multiplyNumbers(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_OUTPUT) {
            inputOutput = instruction[IDX_MODE1] == MODE_IMMEDIATE
                    ? parsedCode[i + 1]
                    : parsedCode[parsedCode[i + 1]];
            System.out.printf("output: %d\n", inputOutput);
            if (inputOutput != 0) {
                System.arraycopy(backup, 0, parsedCode, 0, parsedCode.length);
                System.out.printf("Error on parsedCode[%d]=%d\n", prevInstructionIdx, parsedCode[prevInstructionIdx]);
                i = prevInstructionIdx;
                retryFromCheckpoint(checkPoint, prevInstructionIdx);
            } else{
                System.out.printf("Success on parsedCode[%d]=%d\n", prevInstructionIdx, parsedCode[prevInstructionIdx]);
                System.arraycopy(parsedCode, 0, backup, 0, parsedCode.length);
                i += 2;
                checkPoint = i;
            }
        } else if (instruction[IDX_OPCODE_A] == INSTR_INPUT) {
            System.out.println("INPUT");
            if (instruction[IDX_MODE1] == MODE_IMMEDIATE) {
                throw new RuntimeException("writing parameter in immediate mode");
                //parsedCode[i + 1] = input;
            } else { // MODE_POSITION
                parsedCode[parsedCode[i + 1]] = inputOutput;
            }
            i += 2;
        }
        return i;
    }

    private void retryFromCheckpoint(int checkPoint, int prevInstructionIdx) {
        int operationsCount = (prevInstructionIdx - checkPoint) / 4;
        int[] permutation = new int[operationsCount * 2];
        int idxToSelect = 0;
        permutation[idxToSelect] = 0;
        retryFromCheckpoint(checkPoint, idxToSelect + 1,  operationsCount, permutation);
        permutation[idxToSelect] = 1;
        retryFromCheckpoint(checkPoint, idxToSelect + 1,  operationsCount, permutation);
    }

    private void retryFromCheckpoint(int checkPoint, int idxToSelect, int operationsCount, int[] permutation) {
        if (finished) {
            return;
        }

        if (idxToSelect == operationsCount * 2 - 1) {
            modifyCode(checkPoint, permutation, operationsCount);
            runProgram(checkPoint);
            return;
        }

        permutation[idxToSelect] = 0;
        retryFromCheckpoint(checkPoint, idxToSelect + 1, operationsCount, permutation);

        permutation[idxToSelect] = 1;
        retryFromCheckpoint(checkPoint, idxToSelect + 1, operationsCount, permutation);
    }

    private void modifyCode(int checkPoint, int[] permutation, int operationsCount) {
        for (int i = 0; i < operationsCount; i++) {
            parsedCode[checkPoint] = modifyParameters(i, checkPoint, permutation);
            checkPoint += 4;
        }
        System.out.printf("Trying with: %s%n", Arrays.toString(permutation));
    }

    private int addNumbers(int i, int[] parsedCode, int[] instruction) {
        int a = instruction[IDX_MODE1] == MODE_IMMEDIATE
                ? parsedCode[i + 1]
                : parsedCode[parsedCode[i + 1]];
        int b = instruction[IDX_MODE2] == MODE_IMMEDIATE
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

        if (instruction[IDX_MODE3] == MODE_IMMEDIATE) {
            String errMsg = String.format("Immediate mode on instruction parsedCode[%d]=%d", prevInstructionIdx, code);
            throw new RuntimeException(errMsg);
        }

        return instruction;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
