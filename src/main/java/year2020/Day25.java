package year2020;

import aoc.IAocTask;

import java.util.List;

public class Day25 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_25.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        long cardPublicKey = Long.parseLong(lines.get(0));
        long doorPublicKey = Long.parseLong(lines.get(1));
        long cardLoopSize = transformSubjectNumber(cardPublicKey);
        long encryptionKey = getEncryptionKey(doorPublicKey, cardLoopSize);
        System.out.println(encryptionKey);
    }

    private long getEncryptionKey(long publicKey, long loopSize) {
        long value = 1;
        for (int i = 0; i < loopSize; i++) {
            value = transform(publicKey, value);
        }
        return value;
    }

    private long transformSubjectNumber(long cardPublicKey) {
        int subjectNumber = 7;
        long value = 1;
        for (int i = 0; ; i++) {
            value = transform(subjectNumber, value);
            if (value == cardPublicKey) {
                return i + 1;
            }
        }
    }

    private long transform(long publicKey, long value) {
        value *= publicKey;
        value %= 20201227;
        return value;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        System.out.println("*");
    }
}
