package aoc;

import java.util.List;

public interface IAocTask {

    String getFileName();

    void solvePartOne(List<String> lines);

    void solvePartTwo(List<String> lines);
}
