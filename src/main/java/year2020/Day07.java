package year2020;

import aoc.IAocTask;

import java.util.HashMap;
import java.util.List;

import static year2020.Day07.NO_OTHER;

public class Day07 implements IAocTask {
    HashMap<String, Rule> rules;
    static final String SHINY_GOLD = "shiny gold";
    static final String NO_OTHER = "no other";

    @Override
    public String getFileName() {
        return "aoc2020/input_07.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        rules = new HashMap<>();
        lines.forEach(line -> {
            var rule = Rule.parse(line);
            rules.put(rule.name, rule);
        });
        int colorsContainingGold = 0;
        for (var rule : rules.keySet()) {
            if (canContainGold(rule)) {
                colorsContainingGold++;
            }
        }
        System.out.println(colorsContainingGold);
    }

    private boolean canContainGold(String name) {
        Rule rule = rules.get(name);
        if (rule.subRuleToCount.containsKey(SHINY_GOLD)) {
            return true;
        }
        if (rule.subRuleToCount.containsKey(NO_OTHER)) {
            return false;
        }
        var can = false;
        for (var subRule : rule.subRuleToCount.keySet()) {
            can = can || canContainGold(subRule);
        }
        return can;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        System.out.println(rules);
        System.out.println(countIndividual(SHINY_GOLD));
    }

    private int countIndividual(String name) {
//        System.out.println(name +": ");
        int subCounts = 0;
        var rule = rules.get(name);
//        System.out.println(rule);
        if (rule.subRuleToCount.containsKey(NO_OTHER)) {
            return 0;
        }
        for (var subName: rule.subRuleToCount.keySet()) {
            int subCount = rule.subRuleToCount.get(subName);
            int toAdd = countIndividual(subName);
//            System.out.format("%s %d * %d%n", subName, subCount, toAdd);
            subCounts += subCount;
            subCounts += subCount * toAdd;
//            System.out.format("%s -> %s%n", subName, subCount);
        }
        return subCounts;
    }
}

class Rule {
    String name;
    HashMap<String, Integer> subRuleToCount;

    public Rule(String name, HashMap<String, Integer> subRuleToCount) {
        this.name = name;
        this.subRuleToCount = subRuleToCount;
    }

    public static Rule parse(String line) {
        String[] elements = line.split(" ");
        String name = elements[0] + " " + elements[1];
        String subRules = line.replaceAll(name + " bags contain ", "");
        String[] subSplit = subRules.replace(".", "").split(", ");
        HashMap<String, Integer> subRuleToCount = new HashMap<>();
        for (var subRule : subSplit) {
            String[] countNameBag = subRule.trim().split(" ");
            if (subRule.startsWith(NO_OTHER)) {
                subRuleToCount.put(NO_OTHER, 1);
                break;
            }
            Integer count = Integer.parseInt(countNameBag[0]);
            String subName = countNameBag[1] + " " + countNameBag[2];
            subRuleToCount.put(subName, count);
        }
        return new Rule(name, subRuleToCount);
    }

    @Override
    public String toString() {
        return subRuleToCount.toString();
    }
}
