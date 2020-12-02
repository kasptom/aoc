package year2020;

import aoc.IAocTask;

import java.util.List;

public class Day02 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_02.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        System.out.println(lines.stream()
                .map(PasswordEntry::parse)
                .filter(PasswordEntry::check)
                .count());
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        System.out.println(lines.stream()
                .map(PasswordEntry::parse)
                .filter(PasswordEntry::check2)
                .count());
    }

    static class PasswordEntry {
        int minCount;
        int maxCount;
        char letter;
        String password;

        public PasswordEntry(int minCount, int maxCount, char letter, String password) {
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.letter = letter;
            this.password = password;
        }

        static PasswordEntry parse(String line) {
            String[] limitsLetterPassword = line.split(" ");
            String[] minMax = limitsLetterPassword[0].split("-");
            int minCount = Integer.parseInt(minMax[0]);
            int maxCount = Integer.parseInt(minMax[1]);
            char letter = limitsLetterPassword[1].replace(":", "").charAt(0);
            String password = limitsLetterPassword[2];
            return new PasswordEntry(minCount, maxCount, letter, password);
        }

        boolean check() {
            int count = 0;
            for (int i = 0; i < password.length(); i++) {
                if (password.charAt(i) == letter) {
                    count++;
                }
            }
            return minCount <= count && count <= maxCount;
        }

        boolean check2() {
            return password.charAt(minCount - 1) == letter ^ password.charAt(maxCount - 1) == letter;
        }
    }
}
