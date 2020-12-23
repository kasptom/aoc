import aoc.IAocTask;
import year2020.Day23;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        IAocTask task = new Day23();

        ClassLoader classLoader = task.getClass().getClassLoader();
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource(task.getFileName())).getFile());
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader reader = new BufferedReader(fileReader);
        List<String> lines = reader.lines().collect(Collectors.toList());

        task.solvePartOne(lines);
        task.solvePartTwo(lines);
    }
}
