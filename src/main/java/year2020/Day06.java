package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day06 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_06.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<List<String>> groups = getGroups(lines);
        int sum = groups.stream()
                .map(this::toUniqueCount)
                //.peek(System.out::println)
                .reduce(0, Integer::sum);
        System.out.println(sum);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<List<String>> groups = getGroups(lines);
        int sum = groups.stream()
                .map(this::toAllAnsweredCount)
                //.peek(System.out::println)
                .reduce(0, Integer::sum);
        System.out.println(sum);
    }

    private List<List<String>> getGroups(List<String> lines) {
        List<List<String>> groups = new ArrayList<>();

        List<String> group = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                groups.add(group);
                group = new ArrayList<>();
            } else if (i == lines.size() - 1) {
                group.add(line);
                groups.add(group);
            } else {
                group.add(line);
            }
        }
        return groups;
    }

    private int toUniqueCount(List<String> grp) {
        HashSet<Character> yesAnswers = new HashSet<>();
        for (var personAnswers : grp) {
            yesAnswers.addAll(toAnswerList(personAnswers));
        }
        return yesAnswers.size();
    }

    private int toAllAnsweredCount(List<String> grp) {
        HashMap<Character, Integer> yesAnswersToCount = new HashMap<>();
        for (var personAnswers : grp) {
            List<Character> personLetters = toAnswerList(personAnswers);
            personLetters.forEach(letter -> yesAnswersToCount.merge(letter, 1, Integer::sum));
        }
        return (int) yesAnswersToCount.values().stream().filter(value -> value == grp.size()).count();
    }

    private List<Character> toAnswerList(String personAnswers) {
        return Arrays.stream(personAnswers.trim().split("")).map(letter -> letter.charAt(0)).collect(Collectors.toList());
    }
}
