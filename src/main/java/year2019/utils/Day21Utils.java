package year2019.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day21Utils {
    private static final int LOOK_AHEAD_SIZE = 9;
    private static final int SPRING_BOT_JUMP_RANGE = 4;

    public static void main(String[] args) {
        List<int[]> tilesConfigurations = new ArrayList<>();

        generateCombinations(tilesConfigurations);
        System.out.println("all possibilities count: " + tilesConfigurations.size());

        List<int[]> possibleToJump = tilesConfigurations
                .stream()
                .filter(Day21Utils::isPossibleToPass)
                .collect(Collectors.toCollection(ArrayList::new));

        System.out.printf("Possible to pass through %d%n", possibleToJump.size());

        possibleToJump.forEach(tiles -> System.out.println(Day21Utils.getRobotView(tiles)));
//        System.out.println("|ABCDEFGHI|");
//        tilesConfigurations.forEach(tiles -> System.out.println(Day21Utils.getRobotView(tiles)));
//        Day21Utils.saveAsCsv(tilesConfigurations, String.format("task_21_tiles_%d.csv", LOOK_AHEAD_SIZE));
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
                .map(value -> value == 0 ? "." : "#")
                .reduce((tiles, tile) -> tiles + tile)
                .orElse("ERROR") + "|";
    }

    private static void saveAsCsv(List<int[]> combinations, String csvFileName) {
        Charset charset = StandardCharsets.UTF_8;
        Path csvFilePath = java.nio.file.Paths.get(csvFileName);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(csvFilePath, charset)) {
            String lines = combinations.stream()
                    .map(Day21Utils::mapToCsvRow)
                    .reduce((rows, row) -> rows + "\n" + row)
                    .orElse("ERROR");
            bufferedWriter.write(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String mapToCsvRow(int[] combination) {
        return Arrays.stream(combination)
                .boxed()
                .map(String::valueOf)
                .reduce((tiles, tile) -> tiles + ";" +  tile)
                .orElse("ERROR");
    }

    private static boolean isPossibleToPass(int[] tiles) {
        int robotIdx = -1;
        return isPossibleToPass(true, robotIdx, tiles) || isPossibleToPass(false, robotIdx, tiles);
    }

    private static boolean isPossibleToPass(boolean isJump, int robotIdx, int[] tiles) {
        int nextRobotIdx = isJump ? robotIdx + SPRING_BOT_JUMP_RANGE : robotIdx + 1;
        if (LOOK_AHEAD_SIZE <= nextRobotIdx) return true;

        if (tiles[nextRobotIdx] == 0) return false;

        return isPossibleToPass(false, nextRobotIdx, tiles) || isPossibleToPass(true, nextRobotIdx, tiles);
    }
}
