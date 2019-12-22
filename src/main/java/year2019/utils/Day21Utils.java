package year2019.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day21Utils {
    private static final int LOOK_AHEAD_SIZE = 4;
    private static final int SPRING_BOT_JUMP_RANGE = 4;

    public static void main(String[] args) {
        List<int[]> tilesConfigurations = new ArrayList<>();

        generateCombinations(tilesConfigurations);
        System.out.println("all possibilities count: " + tilesConfigurations.size());

        tilesConfigurations.forEach(tiles -> System.out.println(Day21Utils.getRobotView(tiles)));
    }

    private static void generateCombinations(List<int[]> tilesConfigurations) {
        int[] configuration = new int[LOOK_AHEAD_SIZE];

        int currentIdx = 0;
        generateCombinations(currentIdx, 0, configuration, tilesConfigurations);
        generateCombinations(currentIdx, 1, configuration, tilesConfigurations);
    }

    private static void generateCombinations(int currentIdx, int isGround, int[] option, List<int[]> tilesConfigurations) {
        option[currentIdx] = isGround;
        if (currentIdx == LOOK_AHEAD_SIZE - 1) {
            int[] newOption = new int[LOOK_AHEAD_SIZE];
            System.arraycopy(option, 0, newOption, 0, LOOK_AHEAD_SIZE);
            tilesConfigurations.add(newOption);
            return;
        }

        generateCombinations(currentIdx + 1, 0, option, tilesConfigurations);
        generateCombinations(currentIdx + 1, 1, option, tilesConfigurations);
    }

    private static String getRobotView(int[] tilesBeforeRobot) {
        return "|" + Arrays.stream(tilesBeforeRobot)
                .boxed()
                .map(value -> value == 0 ? " " : "#")
                .reduce((tiles, tile) -> tiles + tile)
                .orElse("ERROR") + "|";
    }
}
