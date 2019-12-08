package year2019;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day08 implements IAocTask {
    private int imageWidth = 25;
    private int imageHeight = 6;
//    private int imageWidth = 3;
//    private int imageHeight = 2;


    @Override
    public String getFileName() {
        return "aoc2019/input_08.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<int[][]> layers = loadImage(lines.get(0));

//        printLayers(layers);

        int layerIdx = getLayerWithFewestZeros(layers);
        System.out.printf("layer: %d%n", layerIdx);

        int onesCount = getCountOfDigitOnLayer(layers, layerIdx, 1);
        int twosCount = getCountOfDigitOnLayer(layers, layerIdx, 2);
        System.out.println(onesCount * twosCount);
    }

    @SuppressWarnings("unused")
    private void printLayers(List<int[][]> layers) {
        for (int[][] layer : layers) {
            printLayer(layer);
            System.out.println();
        }
    }

    private void printLayer(int[][] layer) {
        for (int[] row : layer) {
            String line = Arrays.stream(row)
                    .mapToObj(val -> val == 0 ? "▮" : val == 1 ? "▯" : "T")
                    .reduce((val1, val2) -> String.format("%s%s", val1, val2))
                    .orElse("");
            System.out.println(line);
        }
    }

    private int getCountOfDigitOnLayer(List<int[][]> layers, int layerIdx, int digit) {
        int[][] layer = layers.get(layerIdx);
        int sum = 0;
        for (int[] row : layer) {
            sum += Arrays.stream(row).filter(pixelValue -> pixelValue == digit)
                    .map(pixelValue -> 1)
                    .sum();
        }
        return sum;
    }

    private List<int[][]> loadImage(String encodedImage) {
        List<Integer> oneRowImage = Arrays.stream(encodedImage.split(""))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));

        int layersCount = oneRowImage.size() / (imageHeight * imageWidth);
        List<int[][]> layers = new ArrayList<>();

        for (int layerIdx = 0; layerIdx < layersCount; layerIdx++) {
            int[][] image = new int[imageHeight][imageWidth];
            for (int j = 0; j < imageHeight; j++) {
                for (int k = 0; k < imageWidth; k++) {
                    image[j][k] = oneRowImage.get(layerIdx * (imageHeight * imageWidth) + j * imageWidth + k);
                }
            }
            layers.add(image);
        }

        return layers;
    }

    private int getLayerWithFewestZeros(List<int[][]> layers) {
        int minZerosCount = imageWidth;

        int[] zerosPerLayerCounter = new int[layers.size()];

        int layerIdx = 0;
        for (int[][] layer : layers) {
            for (int i = 0; i < imageHeight; i++) {
                int zerosCount = Arrays.stream(layer[i]).filter(val -> val == 0)
                        .map(val -> 1)
                        .sum();
                zerosPerLayerCounter[layerIdx] += zerosCount;
            }
            layerIdx++;
        }
        for (int i = 0; i < layers.size(); i++) {
            if (zerosPerLayerCounter[i] < minZerosCount) {
                layerIdx = i;
                minZerosCount = zerosPerLayerCounter[i];
            }
        }

        return layerIdx;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<int[][]> layers = loadImage(lines.get(0));
        int[][] merged = new int[imageHeight][imageWidth];
        for (int i = layers.size() - 1; i >= 0; i--) {
            int[][] layer = layers.get(i);
            for (int j = 0; j < imageHeight; j++) {
                for (int k = 0; k < imageWidth; k++) {
                    merged[j][k] = layer[j][k] != 2 ? layer[j][k] : merged[j][k];
                }
            }
        }

        printLayer(merged);
    }
}
