import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 implements IAocTask {

    private static final int SIDE_SIZE = 1000;

    private static final String CLAIM_REGEX = "#([0-9]+) @ ([0-9]+),([0-9]+): ([0-9]+)x([0-9]+)";

    private final HashMap<Integer, Integer> fabricClaims = initializeFrabricClaims();

    private HashMap<Integer, Integer> initializeFrabricClaims() {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < SIDE_SIZE * SIDE_SIZE; i++) {
            map.put(i, 0);
        }

        return map;
    }

    @Override
    public String getFileName() {
        return "input_03.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        Pattern pattern = Pattern.compile(CLAIM_REGEX);
        for (String line : lines) {
            updateFabricClaims(pattern, line);
        }

        System.out.println(fabricClaims.values().stream().filter(claimCount -> claimCount > 1).count());
    }

    private void updateFabricClaims(Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            int claimId = Integer.parseInt(matcher.group(1));
            int leftOffset = Integer.parseInt(matcher.group(2));
            int topOffset = Integer.parseInt(matcher.group(3));
            int width = Integer.parseInt(matcher.group(4));
            int height = Integer.parseInt(matcher.group(5));

            for (int i = leftOffset; i < leftOffset + width; i++) {
                for (int j = topOffset; j < topOffset + height; j++) {
                    int squareInchId = i * SIDE_SIZE + j % SIDE_SIZE;
                    fabricClaims.put(squareInchId, fabricClaims.get(squareInchId) + 1);
                }
            }

        } else {
            throw new RuntimeException(String.format("could not find claim in line: %s", line));
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
