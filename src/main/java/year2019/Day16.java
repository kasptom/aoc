package year2019;

import aoc.IAocTask;

import java.util.*;

public class Day16 implements IAocTask {
    private static final int[] BASE_PATTERN = new int[]{0, 1, 0, -1};

    @Override
    public String getFileName() {
        return "aoc2019/input_16.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        String received = lines.get(0).trim();
        int[] signal = readSignal(received);
        int j;
        System.out.printf("signal length: %d\n", signal.length);
        int phasesNumber = 100;
        HashMap<Integer, List<Integer>> positionToPattern = generatePositionPattern(signal.length);

        for (j = 0; j < phasesNumber; j++) {
            signal = runFlawedFrequencyTransmission(signal, positionToPattern);
//            printDigits(signal, 0, j + 1, signal.length);
//            printSum(signal);
        }

        printDigits(signal, 0, j, 8);
    }

    private void printSum(int[] signal) {
        System.out.printf("sum: %d%n", Arrays.stream(signal).sum());
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        String backup = lines.get(0).trim();
        int phasesNumber = 100;

        HashMap<Integer, List<Integer>> positionToPattern = generatePositionPattern(backup.length() * 20);


        for (int repeats = 2; repeats <= 20; repeats++) { // should be 10k
            System.out.printf("------------- %d%n", repeats);
            // TODO there is no ez way
            String received;
            StringBuilder repeatedSignal = new StringBuilder();
            for (int i = 0; i < repeats; i++) {
                repeatedSignal.append(backup);
            }
            received = repeatedSignal.toString();
            int[] signal = readSignal(received);
            System.out.printf("signal length: %d\n", signal.length);
            // TODO 100 phases SMH
            int j;
            for (j = 0; j < phasesNumber; j++) {
                signal = runFlawedFrequencyTransmission(signal, positionToPattern);
//            printDigits(signal, 0, j + 1, signal.length);
//            printSum(signal);
            }
            // ...

            int offset = getPartTwoOffset(signal);
            offset = 0; // testing
            printDigits(signal, offset, 100, 8);
        }
    }

    private int getPartTwoOffset(int[] signal) {
        StringBuilder offset = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            offset.append(signal[i]);
        }
        return Integer.parseInt(offset.toString());
    }

    private void printEightOffsetDigits(int[] signal, int j) {

    }

    private void printDigits(int[] signal, int offset, int j, int digitsCount) {
        System.out.printf("After %3d phases: ", j);
        for (int i = 0; i < digitsCount; i++) {
            System.out.print(signal[offset + i] + " ");
        }
        System.out.println();
    }

    private int[] readSignal(String received) {
        int[] signal = new int[received.length()];
        final int[] i = {0};
        Arrays.stream(received.trim().split(""))
                .map(Integer::valueOf)
                .forEach(number -> signal[i[0]++] = number);
        return signal;
    }

    private int[] runFlawedFrequencyTransmission(int[] signal, HashMap<Integer, List<Integer>> positionToPattern) {
        int[] transformed = new int[signal.length];
        System.arraycopy(signal, 0, transformed, 0, transformed.length);

        transform(transformed, signal, positionToPattern);

        return transformed;
    }

    private HashMap<Integer, List<Integer>> generatePositionPattern(int length) {
        int position;
        HashMap<Integer, List<Integer>> positionToPattern = new HashMap<>();
        for (int idx = 0; idx < length; idx++) {
            position = idx + 1;
            List<Integer> pattern = getPatternForPosition(position);
            positionToPattern.put(position, pattern);
        }
        return positionToPattern;
    }

    private List<Integer> getPatternForPosition(int repeats) {
        List<Integer> pattern = new ArrayList<>();
        for (Integer value : BASE_PATTERN) {
            for (int i = 0; i < repeats; i++) {
                pattern.add(value);
            }
        }
        Integer removed = pattern.remove(0);
        pattern.add(removed);
        return pattern;
    }

    private void transform(int[] transformed, int[] signal, HashMap<Integer, List<Integer>> positionToPattern) {
        for (int idx = 0; idx < transformed.length; idx++) {
            int position = idx + 1;
            transformed[idx] = transformPosition(signal, positionToPattern.get(position));
        }
    }

    private int transformPosition(int[] signal, List<Integer> pattern) {
        int sum = 0;
        int patternIdx = 0;
        for (int value : signal) {
//            System.out.printf("%d * %d + ", value, pattern.get(patternIdx));
            sum += pattern.get(patternIdx) * value;
            patternIdx = (patternIdx + 1) % pattern.size();
        }
//        System.out.println();
        return Math.abs(sum) % 10;
    }
}
