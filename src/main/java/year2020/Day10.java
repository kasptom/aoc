package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day10 implements IAocTask {
    private final HashMap<Integer, Long> indexToSizeCache = new HashMap<>();

    @Override
    public String getFileName() {
        return "aoc2020/input_10.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Long> ratings = getSortedRatings(lines);
        long oneDiffCount = 0;
        long threeDiffCount = 0;
        for (int i = 1; i < ratings.size(); i++) {
            long diff = ratings.get(i) - ratings.get(i - 1);
            oneDiffCount += diff == 1 ? 1 : 0;
            threeDiffCount += diff == 3 ? 1 : 0;
        }
        System.out.println(oneDiffCount * threeDiffCount);
    }

    private List<Long> getSortedRatings(List<String> lines) {
        List<Long> ratings = lines.stream().map(Long::parseLong).collect(Collectors.toCollection(ArrayList::new));
        ratings.add(0L);
        ratings.add(ratings.stream().max(Long::compareTo).orElse(0L) + 3);
        ratings.sort(Comparator.comparingLong(i -> i));
        return ratings;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Long> ratings = getSortedRatings(lines);
        long possibilities = countPossibilities(ratings, 0);
        System.out.println(possibilities);
    }

    private long countPossibilities(List<Long> ratings, int currentIndex) {
        if (currentIndex == ratings.size() - 1) {
            return 1L;
        }
        var nextSteps = getNextPossibleIndices(ratings, currentIndex);
        if (nextSteps.isEmpty()) {
            return 0L;
        }
        long possibilitiesCount = 0L;
        for (Integer nextStep : nextSteps) {
            if (!indexToSizeCache.containsKey(nextStep)) {
                long countForIndex = countPossibilities(ratings, nextStep);
                possibilitiesCount += countForIndex;
                indexToSizeCache.put(nextStep, countForIndex);
            } else {
                possibilitiesCount += indexToSizeCache.get(nextStep);
            }
        }
        return possibilitiesCount;
    }

    private List<Integer> getNextPossibleIndices(List<Long> ratings, int currentIndex) {
        List<Integer> indices = new ArrayList<>();
        for (int i = currentIndex + 1; i < ratings.size(); i++) {
            long diff = ratings.get(i) - ratings.get(currentIndex);
            if (diff >= 1 && diff <= 3) {
                indices.add(i);
            } else {
                break;
            }
        }
        return indices;
    }
}
