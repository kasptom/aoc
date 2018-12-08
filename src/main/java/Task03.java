import java.util.HashMap;
import java.util.List;

public class Task03 implements IAocTask {

    private HashMap<Integer, Integer> asciiCountMap = new HashMap<>();
    private int doublesCounter = 0;
    private int trippelsCounter = 0;

    @Override
    public String getFileName() {
        return "input_02.txt";
    }

    @Override
    public void solve(List<String> lines) {
        lines.forEach(this::updateCounters);
        System.out.println(String.format("D * T = R, %d * %d = %d", doublesCounter, trippelsCounter, doublesCounter * trippelsCounter));
    }

    private boolean updateCounters(String line) {
        asciiCountMap.clear();
        line.chars().forEach(character -> {
            if (asciiCountMap.containsKey(character)) {
                asciiCountMap.put(character, asciiCountMap.get(character) + 1);
            } else {
                asciiCountMap.put(character, 1);
            }
        });

        if (asciiCountMap.containsValue(2)) {
            doublesCounter++;
        }

        if (asciiCountMap.containsValue(3)) {
            trippelsCounter++;
        }

        return false;
    }
}
