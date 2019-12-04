package year2019;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day04 implements IAocTask {

    private int[] lowerArr;
    private int[] upperArr;
    private int lower;
    private int upper;


    @Override
    public String getFileName() {
        return "aoc2019/input_04.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Integer> range = Arrays.stream(lines.get(0).split("-"))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
        Integer lower = range.get(0);
        Integer upper = range.get(1);

        int count = countNumbersMeetingCriteria(lower, upper);
        System.out.println(count);
    }

    private int countNumbersMeetingCriteria(int lower, int upper) {
        int digitsCount = 6;
        int currentDigitIdx = 0;
        int count = 0;
        int[] number = new int[digitsCount];
        lowerArr = new int[digitsCount];
        upperArr = new int[digitsCount];
        this.lower = lower;
        this.upper = upper;

        int i = digitsCount - 1;
        while (i >= 0) {
            this.lowerArr[i] = lower % 10;
            this.upperArr[i] = upper % 10;
            lower /= 10;
            upper /= 10;
            i--;
        }

        for (i = 1; i <= 9; i++) {
            count += countNumbers(currentDigitIdx, i, digitsCount, number);
        }
        return count;
    }

    private int countNumbers(int currentDigitIdx, int digit, int digitsCount, int[] number) {
        number[currentDigitIdx] = digit;
        if (currentDigitIdx == digitsCount - 1) {
            boolean meetsCriteria = isInRange(number) && hasDouble(number);
            if (meetsCriteria) {
                print(number);
            }
            return meetsCriteria ? 1 : 0;
        }
        int sum = 0;
        for (int i = digit; i <= 9; i++) {
            sum += countNumbers(currentDigitIdx + 1, i, digitsCount, number);
        }
        return sum;
    }

    private void print(int[] number) {
        for (int value : number) {
            System.out.format("%d", value);
        }
        System.out.println();
    }

    private boolean isInRange(int[] numberArr) {
        int number = 0;
        for (int value : numberArr) {
            number *= 10;
            number += value;
        }

        return this.lower <= number && this.upper >= number;
    }

    private boolean hasDouble(int[] number) {
        for (int i = 1; i < number.length; i++) {
            if (number[i - 1] == number[i]) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
