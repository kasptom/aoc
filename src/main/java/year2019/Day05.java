package year2019;

import aoc.IAocTask;

import java.util.*;

import static year2019.utils.Aoc2019Utils.*;

public class Day05 implements IAocTask {

    private int inputOutput;
    private int testFirstInstructionIdx;
    private int testLastInstructionIdx;
    private int testOutputIdx = -1;
    private SortedSet<Integer> outputIndices = new TreeSet<>();
    private int passedTestCounter = 0;
    private int[] parsedCode;
    private int[] backup;
    private boolean finished = false;
    private static final boolean isDebugEnabled = false;

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
        // retryFromCheckpoint(0, 20);
        while (!finished) {
            try {
                printChecksumFor(parsedCode.length);
                runProgram(parsedCode, 1);
                finished = true;
            } catch (RuntimeException exc) {
                printIfEnabled(true, () -> System.out.printf("Repair needed: %s%n", exc.getMessage()));
                try {
                    fixCodeFragment(testFirstInstructionIdx, testLastInstructionIdx);
                } catch (Exception e) {
                    printIfEnabled(true, () -> System.out.println(e.getMessage()));
                    finished = true;
                }
            }
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        parsedCode = loadProgram(lines);
        printChecksumFor(parsedCode.length);
        backup = new int[parsedCode.length];
        try {
            runProgram(parsedCode, 5);
        } catch (Exception exc) {
            System.out.printf("EXCEPTION IN SOLVE PART 2: %s", exc.getMessage());
        }
    }

    /**
     * this is useless :V :V :V ;_;
     **/
    private void fixCodeFragment(int testFirstInstructionIdx, int testLastInstructionIdx) throws Exception {
        printIfEnabled(() -> System.err.printf("trying to fix code between (%d, %d) %n", testFirstInstructionIdx, testLastInstructionIdx));
        System.arraycopy(backup, 0, parsedCode, 0, parsedCode.length);
        List<int[]> opCodePermutations = retryFromCheckpoint(testFirstInstructionIdx, testLastInstructionIdx);
        boolean success = false;
        for (int[] permutation : opCodePermutations) {
            try {
                modifyCode(permutation, testFirstInstructionIdx, testLastInstructionIdx);
                runProgram(parsedCode, 1);
            } catch (RuntimeException exc) {
                printIfEnabled(true, () -> System.out.printf("Repair failed: %s%n", exc.getMessage()));
                if (this.testLastInstructionIdx > testLastInstructionIdx) {
                    testOutputIdx = -1;
                    printIfEnabled(() -> System.out.println("repaired!!!"));
                    this.outputIndices.clear();
                    System.arraycopy(parsedCode, 0, backup, 0, parsedCode.length);
                    success = true;
                    break;
                } else {
                    System.arraycopy(backup, 0, parsedCode, 0, parsedCode.length);
                }
//                printChecksumFor(testFirstInstructionIdx);
                this.testFirstInstructionIdx = 0;
                this.testLastInstructionIdx = 0;
            }
        }

        if (!success) {
            throw new Exception(String.format("No permutations found for (%d, %d)", testFirstInstructionIdx, testLastInstructionIdx));
        }
    }

    @SuppressWarnings("unused")
    private void printChecksumFor(int testFirstInstructionIdx) {
        int checksum = 0;
        int bckChksm = 0;
        for (int i = 0; i < testFirstInstructionIdx; i++) {
            checksum += parsedCode[i];
            bckChksm += backup[i];
        }
        System.out.printf("checksum for %d idx: %d%n", testFirstInstructionIdx, checksum);
        System.out.printf("bckChksm for %d idx: %d%n", testFirstInstructionIdx, bckChksm);
    }

    private void printIfEnabled(PrintAction printAction) {
        printIfEnabled(false, printAction);
    }

    private void printIfEnabled(boolean isForced, PrintAction printAction) {
        if (isDebugEnabled || isForced) {
            printAction.print();
        }
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

    private void runProgram(int[] parsedCode, int inputOutput) {
//        int executionCounter = 0;
        this.inputOutput = inputOutput;
        passedTestCounter = 0;
        testFirstInstructionIdx = 0;
        testLastInstructionIdx = 0;
        for (int i = 0; i < parsedCode.length; /* && executionCounter < 1e9; */) {
            i = runInstructions(parsedCode, i);
//            executionCounter++;
        }
    }

    // https://adventofcode.com/2019/day/5
    private int runInstructions(int[] parsedCode, int i) {
        int[] instruction = getInstruction(parsedCode[i]);

        if (i <= testOutputIdx && outputIndices.contains(i) && instruction[IDX_OPCODE_A] != INSTR_OUTPUT) {
            throw new RuntimeException(String.format("Expected output instruction is missing %d", testOutputIdx));
        }

        if (instruction[IDX_OPCODE_A] == INSTR_ADD) {
            testLastInstructionIdx = i;
            parsedCode[parsedCode[i + 3]] = addNumbers(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_MUL) {
            testLastInstructionIdx = i;
            parsedCode[parsedCode[i + 3]] = multiplyNumbers(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_JMP_TRUE) {
            testLastInstructionIdx = i;
            int jumpIdx = jumpIfTrue(i, parsedCode, instruction);
            i = jumpIdx != -1 ? jumpIdx : i + 3;
        } else if (instruction[IDX_OPCODE_A] == INSTR_JMP_FALSE) {
            testLastInstructionIdx = i;
            int jumpIdx = jumpIfFalse(i, parsedCode, instruction);
            i = jumpIdx != -1 ? jumpIdx : i + 3;
        } else if (instruction[IDX_OPCODE_A] == INSTR_LT) {
            testLastInstructionIdx = i;
            parsedCode[parsedCode[i + 3]] = isLessThan(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_EQ) {
            testLastInstructionIdx = i;
            parsedCode[parsedCode[i + 3]] = isEqual(i, parsedCode, instruction);
            i += 4;
        } else if (instruction[IDX_OPCODE_A] == INSTR_OUTPUT) {
            outputIndices.add(i);
            inputOutput = getParameter(i, parsedCode, instruction, IDX_MODE1, 1);

            // check if next is STOP
            int[] nextInstruction = getInstruction(parsedCode[i + 2]);
            if (isStopInstruction(nextInstruction)) {
                printIfEnabled(true, () -> System.out.printf("TESTS PASSED!!! OUTPUT: %d\n", inputOutput));
                finished = true;
                return parsedCode.length;
            }

            if (inputOutput != 0) {
                System.out.printf("output %d%n", inputOutput);
                testOutputIdx = i;
                throw new RuntimeException(String.format("Output: %d, on for test #%d - instructions(%d, %d)\n",
                        inputOutput, passedTestCounter, testFirstInstructionIdx, testLastInstructionIdx));
            } else {
                passedTestCounter++; // todo max tests passed counter
                printIfEnabled(() -> System.out.printf("Test #%d passed - instructions (%d, %d)\n", passedTestCounter, testFirstInstructionIdx, testLastInstructionIdx));
                i += 2;
                testFirstInstructionIdx = i;
            }
        } else if (instruction[IDX_OPCODE_A] == INSTR_INPUT) {
            printIfEnabled(() -> System.out.println("INPUT"));
            if (instruction[IDX_MODE1] == MODE_IMMEDIATE) {
                throw new RuntimeException("writing parameter in immediate mode");
                //parsedCode[i + 1] = input;
            } else { // MODE_POSITION
                parsedCode[parsedCode[i + 1]] = inputOutput;
            }
            i += 2;
        } else {
            throw new RuntimeException("No valid instruction found");
        }
        return i;
    }

    private boolean isStopInstruction(int[] instruction) {
        return instruction[IDX_OPCODE_A] + 10 * instruction[IDX_OPCODE_B] == INSTR_STOP;
    }

    /**
     * @param startInstructionIdx - index of the first opcode (ADD or MUL) of the failed test
     * @param endInstructionIdx   - index of the last opcode (ADD or MUL) of the failed test
     */
    private List<int[]> retryFromCheckpoint(int startInstructionIdx, int endInstructionIdx) {
        List<int[]> opCodePermutations = new ArrayList<>();
        int operationsCount = getOperationsCount(startInstructionIdx, endInstructionIdx);
        int[] permutation = new int[operationsCount * 2];
        int idxToSelect = 0;
        permutation[idxToSelect] = 0;
        retryFromCheckpoint(opCodePermutations, idxToSelect + 1, operationsCount, permutation);
        permutation[idxToSelect] = 1;
        retryFromCheckpoint(opCodePermutations, idxToSelect + 1, operationsCount, permutation);

        return opCodePermutations;
    }

    private int getOperationsCount(int startInstructionIdx, int endInstructionIdx) {
        return (endInstructionIdx - startInstructionIdx) / 4 + 1;
    }

    private void retryFromCheckpoint(List<int[]> permutations, int idxToSelect, int operationsCount, int[] permutation) {
        if (idxToSelect == operationsCount * 2) {
            int[] newPermutation = new int[permutation.length];
            System.arraycopy(permutation, 0, newPermutation, 0, permutation.length);
            permutations.add(newPermutation);
            return;
        }

        permutation[idxToSelect] = 0;
        retryFromCheckpoint(permutations, idxToSelect + 1, operationsCount, permutation);

        permutation[idxToSelect] = 1;
        retryFromCheckpoint(permutations, idxToSelect + 1, operationsCount, permutation);
    }


    @SuppressWarnings("unused")
    private void printPermutation(int[] permutation) {
        for (Integer value : permutation) {
            printIfEnabled(() -> System.out.print(value));
        }
        printIfEnabled(System.out::println);
    }

    private void modifyCode(int[] permutation, int testFirstInstructionIdx, int testLastInstructionIdx) {
        int instructionIdx = testFirstInstructionIdx;
        int operationsCount = getOperationsCount(testFirstInstructionIdx, testLastInstructionIdx);
        for (int i = 0; i < operationsCount; i++) {
            parsedCode[instructionIdx] = modifyParameters(i, instructionIdx, permutation);
            instructionIdx += 4;
        }
        printIfEnabled(true, () -> System.out.printf("Trying with: %s ", Arrays.toString(permutation)));
        printIfEnabled(true, () -> printOpcodes(this.testFirstInstructionIdx, this.testLastInstructionIdx));
        printIfEnabled(true, this::printOutputsAndRanges);
    }

    private void printOutputsAndRanges() {
        System.out.print(Arrays.toString(outputIndices.toArray()));
        System.out.printf(" F=%d, L=%d, OUTidx=%d%n", testFirstInstructionIdx, testLastInstructionIdx, testOutputIdx);
    }

    private void printOpcodes(int testFirstInstructionIdx, int testLastInstructionIdx) {
        for (int opCodeIdx = testFirstInstructionIdx; opCodeIdx <= testLastInstructionIdx; opCodeIdx += 4) {
            System.out.printf("%d ", parsedCode[opCodeIdx]);
        }
        System.out.println();
    }

    private int addNumbers(int i, int[] parsedCode, int[] instruction) {
        int a = getParameter(i, parsedCode, instruction, IDX_MODE1, 1);
        int b = getParameter(i, parsedCode, instruction, IDX_MODE2, 2);
        return a + b;
    }

    private int multiplyNumbers(int i, int[] parsedCode, int[] instruction) {
        int a = getParameter(i, parsedCode, instruction, IDX_MODE1, 1);
        int b = getParameter(i, parsedCode, instruction, IDX_MODE2, 2);
        return a * b;
    }

    /* part 2 instructions */

    /**
     * Opcode 5 is jump-if-true: if the first parameter is non-zero, it sets the instruction pointer to
     * the value from the second parameter. Otherwise, it does nothing.
     *
     * @return the instruction pointer
     */
    private int jumpIfTrue(int instructionPointer, int[] parsedCode, int[] instruction) {
        int a = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE1, 1);
        int b = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE2, 2);

        return a != 0 ? b : -1;
    }

    /**
     * Opcode 6 is jump-if-false: if the first parameter is zero, it sets the instruction pointer to
     * the value from the second parameter. Otherwise, it does nothing.
     */
    private int jumpIfFalse(int instructionPointer, int[] parsedCode, int[] instruction) {
        int a = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE1, 1);
        int b = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE2, 2);

        return a == 0 ? b : -1;
    }

    /**
     * Opcode 7 is less than: if the first parameter is less than the second parameter,
     * it stores 1 in the position given by the third parameter. Otherwise, it stores 0.
     */
    private int isLessThan(int instructionPointer, int[] parsedCode, int[] instruction) {
        int a = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE1, 1);
        int b = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE2, 2);

        return a < b ? 1 : 0;
    }

    /**
     * Opcode 8 is equals: if the first parameter is equal to the second parameter,
     * it stores 1 in the position given by the third parameter. Otherwise, it stores 0.
     */
    private int isEqual(int instructionPointer, int[] parsedCode, int[] instruction) {
        int a = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE1, 1);
        int b = getParameter(instructionPointer, parsedCode, instruction, IDX_MODE2, 2);

        return a == b ? 1 : 0;
    }

    /**
     * Loads the parameter at offset from the instruction pointer
     *
     * @param instructionPointer the instruction pointer
     * @param parsedCode         the code
     * @param instruction        parsed instruction
     * @param idxMode            mode
     * @param offset             the offset
     * @return the parameter value (immediate or position)
     */
    private int getParameter(int instructionPointer, int[] parsedCode, int[] instruction, int idxMode, int offset) {
        return instruction[idxMode] == MODE_IMMEDIATE
                ? parsedCode[instructionPointer + offset]
                : parsedCode[parsedCode[instructionPointer + offset]];
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
            String errMsg = String.format("Immediate mode on instruction parsedCode[%d]=%d", testLastInstructionIdx, code);
            throw new RuntimeException(errMsg);
        }

        return instruction;
    }


    private interface PrintAction {
        void print();
    }
}
