package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day19 implements IAocTask {

    @Override
    public String getFileName() {
        return "aoc2020/input_19.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int blankLineIdx = lines.indexOf("");
        List<Rule> rules = getRules(lines, blankLineIdx);
        TreeMap<Integer, Rule> idToRule = getRulesMap(rules);
        idToRule.forEach((k, v) -> System.out.println(v));
        long count = getCount(lines, blankLineIdx, idToRule);
        System.out.println(count);
    }

    private TreeMap<Integer, Rule> getRulesMap(List<Rule> rules) {
        return rules.stream().collect(Collectors.toMap(rule -> rule.id, rule -> rule, (x, y) -> x, TreeMap::new));
    }

    private long getCount(List<String> lines, int blankLineIdx, TreeMap<Integer, Rule> idToRule) {
        var rule = idToRule.get(0);
        while (!rule.dependencies.isEmpty()) {
            HashSet<Integer> newDeps = new HashSet<>();
            for (var depId : rule.dependencies) {
                var depRule = idToRule.get(depId);
                String replacement = depRule.text;
                rule.text = rule.text.replaceAll("\\(" + depId + "\\)", "(" + replacement + ")");
                newDeps.addAll(depRule.dependencies);
            }
            rule.dependencies.clear();
            rule.dependencies.addAll(newDeps);
//            System.out.println(rule.text);
        }
//        System.out.println(rule.text);
        return lines.subList(blankLineIdx + 1, lines.size()).stream().filter(line -> line.matches(rule.text)).count();
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int blankLineIdx = lines.indexOf("");
        List<Rule> rules = getRules(lines, blankLineIdx);
        TreeMap<Integer, Rule> idToRule = getRulesMap(rules);

        long bestCount = getCount(lines, blankLineIdx, idToRule);

        int i = 0;
        while (true) {
            rules = getRules(lines, blankLineIdx);
            idToRule = getRulesMap(rules);
            Rule rule8 = Rule.buildRule8(++i);
            idToRule.put(8, rule8);
            long currentCount = getCount(lines, blankLineIdx, idToRule);
            if (currentCount > bestCount) {
                bestCount = currentCount;
            } else {
                i--;
                break;
            }
        }

        Rule rule8 = Rule.buildRule8(i);

        int j = 0;
        while (true) {
            rules = getRules(lines, blankLineIdx);
            idToRule = getRulesMap(rules);
            Rule rule11 = Rule.buildRule11(++j);
            idToRule.put(8, rule8);
            idToRule.put(11, rule11);

            long currentCount = getCount(lines, blankLineIdx, idToRule);
            if (currentCount > bestCount) {
                bestCount = currentCount;
            } else {
                break;
            }
        }
        System.out.println(bestCount);
    }

    private List<Rule> getRules(List<String> lines, int blankLineIdx) {
        return lines.subList(0, blankLineIdx).stream().map(Rule::parse).collect(Collectors.toList());
    }

    static class Rule {
        int id;
        String text;
        Set<Integer> dependencies;

        public Rule(int id, String ruleText, Set<Integer> deps) {
            this.id = id;
            this.text = ruleText;
            this.dependencies = deps;
        }

        static Rule parse(String line) {
            String[] idRuleList = line.split(": "); // 1: 2 3 | 3 2
            int id = Integer.parseInt(idRuleList[0]);  // 1
            String[] ruleList = idRuleList[1].split(" \\| ");
            Set<Integer> deps = new HashSet<>();
            String ruleText;
            for (var rule : ruleList) {
                if (rule.equals("\"a\"") || rule.equals("\"b\"")) {
                    continue;
                }
                deps.addAll(Arrays.stream(rule.split(" ")).map(Integer::valueOf).collect(Collectors.toList()));
            }

            ruleText = !deps.isEmpty()
                    ? line.substring(line.indexOf(":") + 1).replaceAll("(\\d+)", "($1)").replaceAll(" ", "")
                    : line.substring(line.indexOf(":") + 1).replaceAll("\"", "").replaceAll(" ", "");
            return new Rule(id, ruleText, deps);
        }

        public static Rule buildRule8(int repeats) {
            // 8: 42 | 42 8
            int id = 8;
            List<Integer> base = new ArrayList<>(List.of(42));
            List<List<Integer>> rules = new ArrayList<>();
            rules.add(base);
            for (int i = 0; i < repeats; i++) {
                List<Integer> next = new ArrayList<>(rules.get(rules.size() - 1));
                next.add(42);
                rules.add(next);
            }
            String text = rules.stream().map(list -> list.stream().map(rId -> "(" + rId + ")").collect(Collectors.joining())).collect(Collectors.joining("|"));
            HashSet<Integer> deps = new HashSet<>();
            deps.add(42);
            return new Rule(id, text, deps);
        }

        public static Rule buildRule11(int repeats) {
            // 11: 42 31 | 42 11 31
            int id = 11;
            List<Integer> base = new ArrayList<>(List.of(42, 31));
            List<List<Integer>> rules = new ArrayList<>();
            rules.add(base);
            for (int i = 0; i < repeats; i++) {
                List<Integer> next = new ArrayList<>(rules.get(rules.size() - 1));
                next.add(0, 42);
                next.add(31);
                rules.add(next);
            }
            String text = rules.stream().map(list -> list.stream().map(rId -> "(" + rId + ")").collect(Collectors.joining())).collect(Collectors.joining("|"));
            HashSet<Integer> deps = new HashSet<>();
            deps.add(42);
            deps.add(31);
            return new Rule(id, text, deps);
        }

        @Override
        public String toString() {
            return id + ": " + text + ", " + dependencies;
        }
    }
}

