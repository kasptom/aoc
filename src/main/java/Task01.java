import java.util.List;

public class Task01 implements IAocTask {

    @Override
    public String getFileName() {
        return "input_01.txt";
    }

    @Override
    public void solve(List<String> lines) {

        int result = 0;

        for (String line : lines) {
            result += Integer.parseInt(line.trim());
        }

        System.out.println(result);
    }
}
