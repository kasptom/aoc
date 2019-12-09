package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day07 implements IAocTask {
    private long output;

    @Override
    public String getFileName() {
        return "aoc2019/input_07.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        if (getFileName().equals("aoc2019/input_07_small_2.txt")) return;

        Day05 day5 = new Day05();
        long[] parsedCode = Aoc2019Utils.loadProgram(lines);
        long[] backup = new long[parsedCode.length];

        System.arraycopy(parsedCode, 0, backup, 0, parsedCode.length);

        List<int[]> permutations = generateSignalPermutations(0, 4);
        System.out.println(permutations.size());

        long maxOutput = -1;

        for (int[] permutation : permutations) {
            long inputSignal = 0;
            for (int i = 0; i < 5; i++) {
//                System.out.printf("phase: %d, input: %d%n", testPhase[i], inputSignal);
                System.arraycopy(backup, 0, parsedCode, 0, parsedCode.length);
                long[] output = day5.runProgram(parsedCode, new long[]{permutation[i], inputSignal});
                inputSignal = output[0];
//                System.out.println();
            }
            if (maxOutput < inputSignal) {
                maxOutput = inputSignal;
            }
//            System.out.println(inputSignal);
        }
        System.out.println(maxOutput);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        Day05 day5 = new Day05();

        long[] parsedCode = Aoc2019Utils.loadProgram(lines);
        List<int[]> permutations = generateSignalPermutations(5, 9);
        //List<int[]> permutations = new ArrayList<>();
        //permutations.add(new int[]{9, 8, 7, 6, 5});
        System.out.println(permutations.size());

        final long[] maxOutput = {-1};

        for (int[] permutation : permutations) {
            System.out.printf("trying with %s%n", Arrays.toString(permutation));
            List<Amplifier> amplifiers = initializeAmplifiers(parsedCode);
            this.output = 0;
            final int[] i = {0};
            final boolean[] toggle = {true};
            final int[] currentAmplifierId = {0};
            final Amplifier[] currentAmplifier = new Amplifier[1];

            day5.setIoListeners(() -> {
                        long result;
                        if (i[0] < 5) {
                            if (toggle[0]) {
                                result = permutation[i[0]];
                                i[0]++;
                            } else {
                                result = this.output;
                            }
                            toggle[0] = !toggle[0];
                        } else {
                            result = this.output;
                        }
                        System.out.println("in " + result);
                        return result;
                    }, (output, instructionPointer) -> {
                        System.out.println("out " + this.output);
                        this.output = output;
                        currentAmplifier[0].setInstructionPointer(instructionPointer);
                        currentAmplifierId[0] = (currentAmplifierId[0] + 1) % 5;
                        System.out.printf("switching to amplifier %d%n", currentAmplifierId[0]);
                        currentAmplifier[0] = amplifiers.get(currentAmplifierId[0]);
                        day5.runProgram(currentAmplifier[0].getCode(), new long[2], currentAmplifier[0].getInstructionPointer());
                        return true; // should pause
                    },
                    () -> {
                        if (maxOutput[0] < this.output) {
                            maxOutput[0] = this.output;
                        }
                    });

            currentAmplifier[0] = amplifiers.get(currentAmplifierId[0]);
            day5.runProgram(currentAmplifier[0].getCode(), new long[2], 0);
//            System.out.println(inputSignal);
        }
        System.out.println(maxOutput[0]);
    }

    private List<Amplifier> initializeAmplifiers(long[] parsedCode) {
        List<Amplifier> amplifiers;
        amplifiers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            long[] code = new long[parsedCode.length];
            System.arraycopy(parsedCode, 0, code, 0, code.length);
            Amplifier amplifier = new Amplifier(code);
            amplifier.setInstructionPointer(0);
            amplifiers.add(amplifier);
        }
        return amplifiers;
    }

    private List<int[]> generateSignalPermutations(int minPhase, int maxPhase) {
        List<int[]> permutations = new ArrayList<>();
        int currentIndex = 0;
        int permutationLength = 5;
        int[] permutation = new int[permutationLength];
        for (int phase = minPhase; phase <= maxPhase; phase++) {
            generatePermutations(permutations, permutation, currentIndex, phase, minPhase, maxPhase);
        }
        return permutations;
    }

    private void generatePermutations(List<int[]> permutations, int[] permutation, int currentIndex, int phase, int minPhase, int maxPhase) {
        for (int i = 0; i < currentIndex; i++) {
            int prevValue = permutation[i];
            if (prevValue == phase) {
                return; // already used
            }
        }

        permutation[currentIndex] = phase;

        if (currentIndex == permutation.length - 1) {
            int[] newPermutation = new int[permutation.length];
            System.arraycopy(permutation, 0, newPermutation, 0, permutation.length);
            permutations.add(newPermutation);
            return;
        }

        for (int nextPhase = minPhase; nextPhase <= maxPhase; nextPhase++) {
            generatePermutations(permutations, permutation, currentIndex + 1, nextPhase, minPhase, maxPhase);
        }
    }

    static class Amplifier {
        private final long[] code;
        private int instructionPointer;

        public Amplifier(long[] code) {
            this.code = code;
        }

        public long[] getCode() {
            return this.code;
        }

        public void setInstructionPointer(int instructionPointer) {
            this.instructionPointer = instructionPointer;
        }

        public int getInstructionPointer() {
            return this.instructionPointer;
        }
    }
}
