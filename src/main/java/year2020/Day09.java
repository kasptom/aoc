package year2020;

import aoc.IAocTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Day09 implements IAocTask {
    long wrongNumber = -1;
    List<Long> wholeCode;

    @Override
    public String getFileName() {
        return "aoc2020/input_09.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int preambleSize = 25;
        List<Long> preamble = lines.subList(0, preambleSize).stream().map(Long::parseLong)
                .collect(Collectors.toList());
        HashMap<Long, Integer> preambleToCount = new HashMap<>();
        preamble.forEach(number -> preambleToCount.merge(number, 1, Integer::sum));
        List<Long> code = lines.subList(preambleSize, lines.size()).stream().map(Long::parseLong)
                .collect(Collectors.toList());
        long firstWrongNumber = findFirstWrong(preamble, preambleToCount, code);
        wrongNumber = firstWrongNumber;
        System.out.println(firstWrongNumber);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        wholeCode = lines.stream().map(Long::parseLong).collect(Collectors.toList());
        List<Long> contiguous = findContiguous();
        System.out.println(contiguous.stream().max(Long::compareTo).orElse(0L) + contiguous.stream().min(Long::compareTo).orElse(0L));
    }

    private List<Long> findContiguous() {
        for (int size = 2; size <= wholeCode.size(); size++) {
            for (int i = 0; i < wholeCode.size(); i++) {
                int endIdx = i + size;
                if (endIdx > wholeCode.size()) {
                    continue;
                }
                var sublist = wholeCode.subList(i, endIdx);
                long sum = sublist.stream().mapToLong(Long::longValue).sum();
                if (sum == wrongNumber) {
                    return sublist;
                }
            }
        }
        return Collections.emptyList();
    }

    private long findFirstWrong(List<Long> preamble, HashMap<Long, Integer> preambleSet, List<Long> code) {
        for (long next : code) {
            boolean hasSum = false;
            for (int j = 0; j < preamble.size(); j++) {
                if (hasSum(next, preamble, preambleSet, j)) {
                    hasSum = true;
                    long removed = preamble.remove(0);
                    preamble.add(next);
                    preambleSet.put(removed, preambleSet.get(removed) - 1);
                    preambleSet.merge(next, 1, Integer::sum);
                    if (preambleSet.get(removed).equals(0)) {
                        preambleSet.remove(removed);
                    }
                    break;
                }
            }
            if (!hasSum) {
                return next;
            }
        }
        return -1;
    }

    private boolean hasSum(long next, List<Long> preamble, HashMap<Long, Integer> preambleSet, int j) {
        long number = preamble.get(j); // next = number + toFind
        return preambleSet.containsKey(next - number);
    }
}
