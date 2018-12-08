import java.util.HashSet;
import java.util.List;

public class Task02 implements IAocTask {

    private HashSet<Integer> frequencies = new HashSet<>();

    @Override
    public String getFileName() {
        return "input_01.txt";
    }

    @Override
    public void solve(List<String> lines) {

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
