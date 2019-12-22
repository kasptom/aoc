package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Day12 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2019/input_12.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Moon> moons = loadMoons(lines);
        int steps = 1000;
        int energy = calculateEnergy(moons, steps);
        System.out.println(energy);
    }

    private int calculateEnergy(List<Moon> moons, int steps) {
//        System.out.printf("--- step %d ---%n", 0);
        moons.forEach(System.out::println);
        for (int step = 1; step <= steps; step++) {
            for (int axis = 0; axis < 3; axis++)
                refreshPositionsAndVelocities(moons, axis);
        }
        return moons.stream().map(this::calculateMoonEnergy).reduce(Integer::sum).orElse(-1);
    }

    private void refreshPositionsAndVelocities(List<Moon> moons, int axis) {
        List<Moon> prevState = new ArrayList<>(moons);
        for (Moon moon : prevState) {
            for (Moon other : prevState.stream().filter(m -> m.id != moon.id).collect(Collectors.toCollection(ArrayList::new))) {
                if (moon.id < other.id) continue;
                Moon updatedMoon = moons.get(moon.id - 1);
                Moon updatedOther = moons.get(other.id - 1);

                int[] velocity = updatedMoon.velocity;
                int[] otherVel = updatedOther.velocity;

                velocity[axis] += Integer.compare(other.position[axis], moon.position[axis]);
                otherVel[axis] += Integer.compare(moon.position[axis], other.position[axis]);
            }
        }
        moons.forEach(moon -> updateMoonPosition(moon, axis));
        //System.out.printf("--- step %d ---%n", step);
//        moons.forEach(System.out::println);
    }

    private void updateMoonPosition(Moon moon, int axis) {
        moon.position[axis] += moon.velocity[axis];
    }

    private int calculateMoonEnergy(Moon m) {
        return Arrays.stream(m.velocity).map(Math::abs).sum() * Arrays.stream(m.position).map(Math::abs).sum();
    }

    private List<Moon> loadMoons(List<String> lines) {
        List<Moon> moons = lines.stream().map(this::createMoon).collect(Collectors.toCollection(ArrayList::new));
        final int[] id = {1};
        moons.forEach(moon -> moon.id = id[0]++);
        return moons;
    }

    private Moon createMoon(String in) {
        in = in.substring(1, in.length() - 1);
        String[] coordPositions = in.split(",");
        int[] position = new int[3];
        int i = 0;
        for (String coordPosition : coordPositions) {
            String[] splitted = coordPosition.split("=");
            position[i++] = Integer.parseInt(splitted[1]);
        }

        return new Moon(position);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Moon> moons = loadMoons(lines);
        List<Moon> initialState = loadMoons(lines);
        long cycles = getCyclePeriod(moons, initialState);
        System.out.println(cycles);
    }

    private long getCyclePeriod(List<Moon> moons, List<Moon> initialState) {
        long[] cycleLengths = {1L, 1L, 1L};
        for (int axis = 0; axis < 3; axis++) {
            refreshPositionsAndVelocities(moons, axis);
            while (!isSame(moons, initialState, axis)) {
                refreshPositionsAndVelocities(moons, axis);
                cycleLengths[axis]++;
            }
        }
        System.out.println(Arrays.toString(cycleLengths));
        Map<Integer, Integer> divisors = createDivisors(cycleLengths);
        final long[] lcm = {1};
        System.out.println("--- divisors ---");
        divisors.forEach((divisor, count) -> {
            System.out.printf("%d: %d%n", divisor, count);
            for (int i = 0; i < count; i++) {
                lcm[0] *= divisor;
            }
        });
        System.out.println();

        return lcm[0];
    }

    private Map<Integer, Integer> createDivisors(long[] cycleLengths) {
        TreeSet<Integer> sieve = Aoc2019Utils.createSieve((int) Arrays.stream(cycleLengths).max().orElse(-1));
        //System.out.println(sieve);
        TreeMap<Integer, Integer> primeDivisorsCounter = new TreeMap<>();
        for (long cycle : cycleLengths) {
            updateCommonDivisors(cycle, primeDivisorsCounter, sieve);
        }
        return primeDivisorsCounter;
    }

    private void updateCommonDivisors(long cycle, TreeMap<Integer, Integer> primeDivisorsCounter, TreeSet<Integer> sieve) {
        for (Integer prime : sieve) {
            if (prime > cycle) {
                return;
            }

            int factorRepeatCount = 0;
            while (cycle > 0) {
                if (cycle % prime == 0) {
                    cycle /= prime;
                    factorRepeatCount++;
                } else {
                    break;
                }
            }

            Integer count = primeDivisorsCounter.get(prime);
            if (count == null || count < factorRepeatCount) {
                primeDivisorsCounter.put(prime, factorRepeatCount);
            }
        }
    }

    private boolean isSame(List<Moon> moons, List<Moon> initialState, int axis) {
        for (Moon moon : moons) {
            Moon initial = initialState.get(moon.id - 1);
            if (initial.velocity[axis] != moon.velocity[axis] || initial.position[axis] != moon.position[axis]) {
                return false;
            }
        }
        return true;
    }

    static class Moon {
        int id;
        int[] position;
        int[] velocity;

        public Moon(int[] position) {
            assert position.length == 3;
            this.position = position;
            this.velocity = new int[3];
        }

        @Override
        public String toString() {
            return String.format("pos<x=%4d, y=%4d, z=%4d>, vel=<x=%4d, y=%4d, z=%4d>",
                    position[0], position[1], position[2],
                    velocity[0], velocity[1], velocity[2]);
        }
    }
}
