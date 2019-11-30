import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class Day01 implements IAocTask {

    private HashSet<Integer> frequencies = new HashSet<>();

    @Override
    public String getFileName() {
        return "aoc2018/input_01.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {

        int result = 0;

        for (String line : lines) {
            result += Integer.parseInt(line.trim());
        }

        System.out.println(result);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int currentFrequency = 0;
        frequencies.add(currentFrequency);

        for (int i = 0; i < lines.size(); i = (i + 1) % lines.size()) {
            currentFrequency += Integer.parseInt(lines.get(i).trim());
            if (frequencies.contains(currentFrequency)) {
                System.out.println(currentFrequency);
                break;
            }
            frequencies.add(currentFrequency);
        }
    }
}
