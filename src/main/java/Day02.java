import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Day02 implements IAocTask {

    private HashMap<Integer, Integer> asciiCountMap = new HashMap<>();

    private HashSet<String> boxIdsWithoutOneLetter = new HashSet<>();

    private int doublesCounter = 0;
    private int triplesCounter = 0;

    @Override
    public String getFileName() {
        return "aoc2018/input_02.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        lines.forEach(this::updateCounters);
        System.out.println(String.format("D * T = R, %d * %d = %d", doublesCounter, triplesCounter, doublesCounter * triplesCounter));
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int idLength = lines.get(0).length();

        for (int i = 0; i < idLength; i++) {
            findCommonIdsExcludingCharacterAt(i, lines);
        }
    }

    private void findCommonIdsExcludingCharacterAt(int characterPosition, List<String> lines) {
        boxIdsWithoutOneLetter.clear();
        for (String line : lines) {
            String transformedId = line.substring(0, characterPosition);
            if (line.length() - 1 != characterPosition) {
                transformedId = transformedId + line.substring(characterPosition + 1);
            }

            if (boxIdsWithoutOneLetter.contains(transformedId)) {
                System.out.println(transformedId);
                break;
            }

            boxIdsWithoutOneLetter.add(transformedId);
        }
    }

    private void updateCounters(String line) {
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
            triplesCounter++;
        }
    }
}
