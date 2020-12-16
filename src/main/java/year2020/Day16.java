package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day16 implements IAocTask {
    List<Ticket> invalid = new ArrayList<>();
    List<Ticket> nearby;

    @Override
    public String getFileName() {
        return "aoc2020/input_16.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int rulesEndIdx = lines.indexOf("your ticket:") - 1;
        int myTicketEndIdx = lines.indexOf("nearby tickets:") - 1;
        List<Day16.Rule> rules = lines.subList(0, rulesEndIdx).stream().map(Day16.Rule::parse)
                .collect(Collectors.toList());
        nearby = lines.subList(myTicketEndIdx + 2, lines.size())
                .stream()
                .map(Ticket::parse)
                .collect(Collectors.toList());
        for (int i = 1; i <= nearby.size(); i++) {
            nearby.get(i - 1).setId(i);
        }

        int invalidSum = sumInvalidIn(nearby, rules);
        System.out.println(invalidSum);
    }

    private int sumInvalidIn(List<Ticket> nearby, List<Rule> rules) {
        int sum = 0;
        for (var ticket : nearby) {
            var ticketInvalidSum = ticket.sumInvalid(rules);
            if (ticket.existsFieldWithNoFittingRules(rules)) {
                invalid.add(ticket);
            }
            sum += ticketInvalidSum;
        }
        return sum;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int rulesEndIdx = lines.indexOf("your ticket:") - 1;
        int myTicketEndIdx = lines.indexOf("nearby tickets:") - 1;
        List<Day16.Rule> rules = lines.subList(0, rulesEndIdx).stream().map(Day16.Rule::parse)
                .collect(Collectors.toList());
        Ticket myTicket = Ticket.parse(lines.get(rulesEndIdx + 2));
//        nearby.add(myTicket);
        nearby.removeAll(invalid);
        List<String> namesOrder = getNamesOrder(nearby, rules);
        System.out.println(namesOrder);

        long product = multiplyDepartures(myTicket, namesOrder);
        System.out.println(product);
    }

    private List<String> getNamesOrder(List<Ticket> tickets, List<Rule> rules) {
        HashMap<Integer, List<String>> indexToName = new HashMap<>();
        TreeSet<Integer> used = new TreeSet<>();
        for (int fieldIdx = 0; fieldIdx < tickets.get(0).fields.size(); fieldIdx++) {
            for (var rule : rules) {
//                System.out.println(rule);
                int count = 0;
                for (var ticket : tickets) {
                    var field = ticket.fields.get(fieldIdx);
//                    System.out.println("Field " + field );
                    if (ticket.isValid(field, List.of(rule))) {
                        count++;
                    }
                }
                if (count == tickets.size()) {
//                    System.out.println("---OK---");
                    indexToName.putIfAbsent(fieldIdx, new ArrayList<>());
                    indexToName.get(fieldIdx).add(rule.name);
//                    rules.remove(rule);
//                    break;
                    used.add(fieldIdx);
                }
            }
        }
        List<String> names = removeCollisions(indexToName);
        return names;
    }

    private List<String> removeCollisions(HashMap<Integer, List<String>> indexToName) {
        TreeMap<Integer, String> fixedIndexToName = new TreeMap<>();
        List<Integer> keys = new ArrayList<>(indexToName.keySet());
        while (!keys.isEmpty()) {
            Integer removed = -1;
            for (int i = 0; i < keys.size(); i++) {
                var key = keys.get(i);
                if (indexToName.get(key).size() == 1) {
                    removed = key;
                    var name = indexToName.get(removed).get(0);
                    fixedIndexToName.put(removed, name);
                    indexToName.values().forEach(value -> value.remove(name));
                    keys.remove(removed);
                    break;
                }
            }
        }
        var result = new ArrayList<>(fixedIndexToName.values());
        return result;
    }

    private long multiplyDepartures(Ticket myTicket, List<String> namesOrder) {
        long product = 1;
        for (int i = 0; i < namesOrder.size(); i++) {
            var field = myTicket.fields.get(i);
            if (namesOrder.get(i).startsWith("departure")) {
                product *= field;
            }
        }
        return product;
    }

    static class Rule {
        String name;
        List<Integer> minRanges;
        List<Integer> maxRanges;

        public Rule(String name, List<Integer> minRanges, List<Integer> maxRanges) {
            this.name = name;
            this.minRanges = minRanges;
            this.maxRanges = maxRanges;
        }

        static Rule parse(String line) {
            String[] nameRanges = line.split(": ");
            String name = nameRanges[0];
            String[] ranges = nameRanges[1].split(" or ");
            var min = new ArrayList<Integer>();
            var max = new ArrayList<Integer>();
            for (var range : ranges) {
                var minMax = range.split("-");
                min.add(Integer.parseInt(minMax[0]));
                max.add(Integer.parseInt(minMax[1]));
            }
            return new Day16.Rule(name, min, max);
        }

        public boolean isValid(int field) {
            for (int i = 0; i < maxRanges.size(); i++) {
                var min = minRanges.get(i);
                var max = maxRanges.get(i);
                if (min <= field && field <= max) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "name='" + name + '\'' +
                    ", minRanges=" + minRanges +
                    ", maxRanges=" + maxRanges +
                    '}';
        }
    }

    static class Ticket {
        int id;
        private final List<Integer> fields;

        Ticket(List<Integer> fields) {
            this.fields = fields;
        }

        static Ticket parse(String line) {
            var fields = Arrays.stream(line.split(",")).map(Integer::valueOf).collect(Collectors.toList());
//            if (fields.stream().anyMatch(x -> x <= 0)) {
//                throw new RuntimeException(line + " field value <= 0: " + fields.stream().filter(x -> x <= 0).collect(Collectors.toList()));
//            }
            return new Ticket(fields);
        }

        public int sumInvalid(List<Rule> rules) {
            int sum = 0;
            for (var field : fields) {
                if (!isValid(field, rules)) {
                    sum += field;
                }
            }
            return sum;
        }

        public boolean existsFieldWithNoFittingRules(List<Rule> rules) {
            for (var field : fields) {
                boolean found = false;
                for (var rule: rules) {
                    if (rule.isValid(field)) {
                        found = true;
                    }
                }
                if (!found) {
                    return true;
                }
            }
            return false;
        }

        private boolean isValid(int field, List<Rule> rules) {
            for (var rule : rules) {
                if (rule.isValid(field)) {
                    return true;
                }
            }
            return false;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Ticket ticket = (Ticket) o;

            return id == ticket.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "T " +
                    "id=" + id +
                    ", "+ fields +
                    ' ';
        }
    }
}


