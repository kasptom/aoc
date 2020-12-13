package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day13 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_13.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int timestamp = Integer.parseInt(lines.get(0));
        List<Integer> buses = getBuses(lines);
        Map<Integer, Integer> busToNextArrival = getNextArrivals(buses, timestamp);
        int bestBus = busToNextArrival
                .keySet()
                .stream()
                .min(Comparator.comparing(busToNextArrival::get))
                .orElse(-1);

        System.out.println(bestBus * (busToNextArrival.get(bestBus) - timestamp));
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Integer> buses = getBuses(lines);
        long earliestTimestamp = getNearestTimestampWithConsecutiveArrivals(buses);
        System.out.println(earliestTimestamp);
    }

    private List<Integer> getBuses(List<String> lines) {
        return Arrays.stream(lines.get(1).split(","))
                .map(bus -> bus.equals("x") ? 0 : Integer.parseInt(bus)).collect(Collectors.toList());
    }

    private Map<Integer, Integer> getNextArrivals(List<Integer> buses, int timestamp) {
        return buses
                .stream()
                .collect(Collectors.toMap(bus -> bus, bus -> getNextArrival(bus, timestamp), Integer::sum));
    }

    private int getNextArrival(int bus, int timestamp) {
        if (bus == 0) return Integer.MAX_VALUE;
        return timestamp + (bus - (timestamp % bus));
    }

    public long getNearestTimestampWithConsecutiveArrivals(List<Integer> buses) {
        TreeMap<Integer, Integer> busToIndex = buses
                .stream()
                .filter(bus -> bus != 0)
                .collect(Collectors.toMap(Function.identity(), buses::indexOf, Integer::sum, TreeMap::new));
        TreeMap<Integer, Integer> indexToBus = buses
                .stream()
                .filter(bus -> bus != 0)
                .collect(Collectors.toMap(buses::indexOf, Function.identity(), Integer::sum, TreeMap::new));

        List<Integer> filteredBuses = buses.stream().filter(bus -> bus != 0).collect(Collectors.toList());
        printHeader(indexToBus, filteredBuses);

        long toAdd = buses.get(0);
        long timestamp = buses.get(0);
        for (int problemSize = 2; problemSize <= filteredBuses.size(); problemSize++) {
            for (; ; timestamp += toAdd) {
                if (modsOk(timestamp, filteredBuses.subList(0, problemSize), busToIndex)) {
                    toAdd *= filteredBuses.get(problemSize - 1);
                    System.out.format("%d / %d = %d%n", timestamp, buses.get(0), timestamp / buses.get(0));
                    printState(filteredBuses, timestamp, busToIndex);
                    break;
                }
            }
        }
        return timestamp;
    }

    private void printHeader(TreeMap<Integer, Integer> indexToBus, List<Integer> filteredBuses) {
        System.out.println(filteredBuses);
        indexToBus.forEach((index, bus) -> System.out.format("(x + %d) %% %d == 0%n", index, bus));
    }

    private boolean modsOk(long timestamp, List<Integer> filteredBuses, TreeMap<Integer, Integer> busToIndex) {
        for (var bus : filteredBuses) {
            var index = busToIndex.get(bus);
            if (!((timestamp + index) % bus == 0)) {
                return false;
            }
        }
        return true;
    }

    private void printState(List<Integer> filteredBuses, long next, TreeMap<Integer, Integer> busToIndex) {
        filteredBuses.forEach(bus -> System.out.format("(%d + %d) %% %d == 0 %s %n", next, busToIndex.get(bus), bus, getState(next, busToIndex, bus)));
        System.out.println();
    }

    private String getState(long next, TreeMap<Integer, Integer> busToIndex, Integer bus) {
        return (next + busToIndex.get(bus)) % bus == 0 ? "✔" : "❌";
    }
}
