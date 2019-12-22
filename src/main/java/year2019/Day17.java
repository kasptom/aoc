package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;
import year2019.utils.Pair;
import year2019.utils.TwoDirNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day17 implements IAocTask {
    private String scaffoldsView = "";
    char[][] twoDimView;

    @Override
    public String getFileName() {
        return "aoc2019/input_17.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        long[] program = Aoc2019Utils.loadProgram(lines);
        program = Aoc2019Utils.copyToLargerMemory(program, 6000);
        Day05 computer = new Day05();
        computer.setIoListeners(() -> 0, (value, iPointer) -> {
            scaffoldsView += getAscii(value);
            return false;
        }, () -> {
//            System.out.printf("Done. Scaffolds view: %n%s%n", scaffoldsView)
        });
        computer.runProgram(program, new long[2]);

        //scaffoldsView = scaffoldsView.substring(1) + "\n";
        twoDimView = getTwoDimView(scaffoldsView);
        //char[][] twoDimView = getTwoDimView(getTestView());

        List<Integer> alignmentParams = getAlignmentParameters(twoDimView);
        printTwoDimView(twoDimView);
        Integer alignmentParametersSum = alignmentParams.stream().reduce(Integer::sum).orElse(-1);
        System.out.printf("Crossings: %d%n", alignmentParams.size());
        System.out.printf("Alignment params sum: %d%n", alignmentParametersSum);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        System.out.println("------- part 2 -----------");
        long[] program = Aoc2019Utils.loadProgram(lines);
        program = Aoc2019Utils.copyToLargerMemory(program, 6000);
        wakeUpTheRobot(program);

        List<Move> allMovesSequence = getAllMovesSequence();
        String allMovesCodes = allMovesSequence.stream()
                .map(Move::toString)
                .reduce((moves, moveB) -> moves + "," + moveB)
                .orElse("");

        HashMap<String, String> instructionNameToLrSequence = divideToInstructions(allMovesCodes);
        System.out.println("all moves " + allMovesSequence);

        instructionNameToLrSequence.forEach((key, value) -> System.out.printf("%s: %s%n", key, value));

        System.out.println(allMovesCodes);

        String[] abcInstructions = new String[] {allMovesCodes};
        instructionNameToLrSequence.forEach((key, value) -> abcInstructions[0] = abcInstructions[0].replaceAll(value, key));
        System.out.println(abcInstructions[0]);
        // TODO odpalenie komend A, B, C

        Day05 computer = new Day05();

        char[] asciiAbcInstructions = addNewLine(abcInstructions[0]).toCharArray();
        char[] asciiA = addNewLine(instructionNameToLrSequence.get("A")).toCharArray();
        char[] asciiB = addNewLine(instructionNameToLrSequence.get("B")).toCharArray();
        char[] asciiC = addNewLine(instructionNameToLrSequence.get("C")).toCharArray();

        char[] input = new char[asciiAbcInstructions.length + asciiA.length + asciiB.length + asciiC.length + 2];
        System.arraycopy(asciiAbcInstructions, 0, input, 0, asciiAbcInstructions.length);
        System.arraycopy(asciiA, 0, input, asciiAbcInstructions.length, asciiA.length);
        System.arraycopy(asciiB, 0, input, asciiAbcInstructions.length + asciiA.length, asciiB.length);
        System.arraycopy(asciiC, 0, input, asciiAbcInstructions.length + asciiA.length + asciiB.length, asciiC.length);
        input[input.length - 2] = 'n';
        input[input.length - 1] = '\n';
        final int[] idx = {0};

        long[] lastOutput = new long[1];
        computer.setIoListeners(() -> {
            //System.out.printf("sending: input[%d] = %c%n", idx[0], input[idx[0]]);
            return input[idx[0]++];
        }, (output, instructionPointer) -> {
            //System.out.printf("output: %d, ip: %d%n", output, instructionPointer);
            lastOutput[0] = output;
            return false;
        }, () -> System.out.printf("stop, collected dust: %d%n", lastOutput[0]));

        computer.runProgram(program, new long[2]);
    }

    private String addNewLine(String abcInstructions) {
        return abcInstructions + "\n";
    }

    private HashMap<String, String> divideToInstructions(String allMovesCodes) {
        HashMap<String, String> instructionToMoves = new HashMap<>();
        String[] instructionNames = new String[] {"A", "B", "C"};

        for(String instructionName : instructionNames) {
            String moveCodes = getLongestRepeatingSequence(allMovesCodes);
            allMovesCodes = allMovesCodes.replaceFirst(moveCodes, "");
            allMovesCodes = allMovesCodes.replaceAll(moveCodes, "X");

            moveCodes = moveCodes.endsWith(",") ? moveCodes.substring(0, moveCodes.length() - 1) : moveCodes;
            instructionToMoves.put(instructionName, moveCodes);
        }

        return instructionToMoves;
    }

    private String getLongestRepeatingSequence(String movesCopy) {
        int startIdx = 0;
        while (movesCopy.charAt(startIdx) == 'X') {
            startIdx++;
        }

        int endIdx = startIdx;
        String currentLongest = movesCopy.substring(startIdx, endIdx + 1);

        while (endIdx < movesCopy.length()) {
            if (movesCopy.length() == endIdx + 1 || movesCopy.charAt(endIdx + 1) == 'X') {
                return currentLongest;
            }

            String nextLongest = movesCopy.substring(startIdx, endIdx + 2);
            if (movesCopy.substring(endIdx + 2, movesCopy.length() - 1).contains(nextLongest)) {
                currentLongest = nextLongest;
                endIdx++;
            } else {
                break;
            }
        }

        return currentLongest;
    }


    private List<Move> getAllMovesSequence() {
        ArrayList<Move> moves = new ArrayList<>();
        Pair<Integer> initialRobotPosition = new Pair<>(0, 0);
        for (int i = 0; i < twoDimView.length; i++) {
            for (int j = 0; j < twoDimView[0].length; j++) {
                if (twoDimView[i][j] == '^') {
                    initialRobotPosition.x = j;
                    initialRobotPosition.y = i;
                    break;
                }
            }
        }
        System.out.println(initialRobotPosition);

        Direction fromDirection = Direction.NORTH;
        Pair<Integer> currentPosition = new Pair<>(initialRobotPosition);
        while(!isEndReached(twoDimView, initialRobotPosition, currentPosition)) {
            Direction direction = getTurnDirection(currentPosition, fromDirection);
            int stepsToNextTurn = countStepsToNextTurn(direction, currentPosition);
            Move move = new Move(fromDirection, direction, stepsToNextTurn);
            moves.add(move);

            moveToNextTurn(currentPosition, direction, stepsToNextTurn);

            System.out.println(moves);
            fromDirection = direction;
        }
        System.out.printf("end reached: %s%n", currentPosition);
        twoDimView[currentPosition.y][currentPosition.x] = 'X';
        printTwoDimView(twoDimView);

        return moves;
    }

    private void moveToNextTurn(Pair<Integer> currentPosition, Direction direction, int stepsToNextTurn) {
        currentPosition.x += direction.dir.x * stepsToNextTurn;
        currentPosition.y += direction.dir.y * stepsToNextTurn;
    }

    private Direction getTurnDirection(Pair<Integer> currentPosition, Direction fromDirection) {
        int optionsCount = 0;

        Direction turnDirection = null;
        for(Direction direction : Direction.values()) {
            if (fromDirection.opposite().equals(direction)) continue;
            int directionXToCheck = currentPosition.x + direction.dir.x;
            int directionYToCheck = currentPosition.y + direction.dir.y;

            if (directionXToCheck == -1 || directionYToCheck == -1 || directionXToCheck == twoDimView[0].length || directionYToCheck == twoDimView.length) continue;

            if (twoDimView[currentPosition.y + direction.dir.y][currentPosition.x + direction.dir.x] == '#') {
                turnDirection = direction;
                optionsCount++;
            }
            if (optionsCount > 1) throw new RuntimeException("This is not a turn!");
        }

        if (optionsCount == 0) throw new RuntimeException("Could not find possible turn");
        return turnDirection;
    }

    private int countStepsToNextTurn(Direction dir, Pair<Integer> currentPosition) {
        Pair<Integer> nextPosition = new Pair<>(currentPosition);
        nextPosition.x += dir.dir.x;
        nextPosition.y += dir.dir.y;
        while (true) {
            int xToCheck = nextPosition.x + dir.dir.x;
            int yToCheck = nextPosition.y + dir.dir.y;
            if (xToCheck == twoDimView[0].length || xToCheck == -1 || yToCheck == twoDimView.length || yToCheck == -1) {
                break;
            }

            if (twoDimView[yToCheck][xToCheck] == '#') {
                nextPosition.x = xToCheck;
                nextPosition.y = yToCheck;
            } else break;
        }
        return Math.abs(nextPosition.x - currentPosition.x + nextPosition.y - currentPosition.y);
    }

    private boolean isEndReached(char[][] twoDimView, Pair<Integer> initialPosition, Pair<Integer> currentPosition) {
        if (initialPosition.equals(currentPosition)) {
            return false;
        }

        if (twoDimView[currentPosition.y][currentPosition.x] != '#') {
            throw new RuntimeException("Vacuum robot is off the scaffolding!");
        }

        return isEndAt(currentPosition);
    }

    private boolean isEndAt(Pair<Integer> currentPosition) {
        int neighbouringScaffoldingCount = 0;
        for (Direction direction : Direction.values()) {
            if (isOutOfRange(twoDimView, currentPosition.y, currentPosition.x, direction)) {
                continue;
            }
            if (twoDimView[currentPosition.y +direction.dir.y][currentPosition.x + direction.dir.x] == '#') {
                neighbouringScaffoldingCount++;
            }

            if (neighbouringScaffoldingCount > 1) return false;
        }

        return neighbouringScaffoldingCount == 1;
    }

    private void wakeUpTheRobot(long[] program) {
        program[0] = 2;
    }

    private void printTwoDimView(char[][] twoDimView) {
        for (char[] chars : twoDimView) {
            for (int j = 0; j < twoDimView[0].length - 1; j++) {
                System.out.print(chars[j]);
            }
            System.out.println();
        }
    }

    private char[][] getTwoDimView(String scaffoldsView) {
        int columns = scaffoldsView.indexOf("\n") + 1;
        int rows = scaffoldsView.length() / columns;
        char[][] twoDimView = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                twoDimView[i][j] = scaffoldsView.charAt(i * columns + j);
            }
        }
        return twoDimView;
    }

    private List<Integer> getAlignmentParameters(char[][] twoDimView) {
        List<Integer> alignmentParameters = new ArrayList<>();
        for (int i = 1; i < twoDimView.length - 1; i++) {
            for (int j = 1; j < twoDimView[0].length - 1; j++) {
                if (isCrossingAt(i, j, twoDimView)) {
                    alignmentParameters.add(i * j);
                }
            }
        }
        return alignmentParameters;
    }

    private boolean isCrossingAt(int i, int j, char[][] twoDimView) {
        if (twoDimView[i][j] != '#') return false;
        for (Direction direction : Direction.values()) {
            if (isOutOfRange(twoDimView, i, j, direction)) {
                continue;
            }
            if (twoDimView[i + direction.dir.y][j + direction.dir.x] != '#') {
                return false;
            }
        }
        return true;
    }

    private boolean isOutOfRange(char[][] twoDimView, int i, int j, Direction direction) {
        int yToCheck = i + direction.dir.y;
        int xToCheck = j + direction.dir.x;
        return xToCheck == -1 || yToCheck == -1 || xToCheck == twoDimView[0].length || yToCheck == twoDimView.length;
    }

    private String getAscii(long value) {
        return Character.valueOf((char) value).toString();
    }

    @SuppressWarnings("unused")
    private String getTestView() {
        return "..#..........\n" +
                "..#..........\n" +
                "##O####...###\n" +
                "#.#...#...#.#\n" +
                "##O###O###O##\n" +
                "..#...#...#..\n" +
                "..#####...^..".replaceAll("O", "#");
    }

    enum Turn {
        LEFT("L"), RIGHT("R");
        String code;

        Turn(String turnCode) {
            this.code = turnCode;
        }
    }

    enum Direction {
        NORTH(new Pair<>(0, -1)), SOUTH(new Pair<>(0, 1)), EAST(new Pair<>(1, 0)), WEST(new Pair<>(-1, 0));

        private final Pair<Integer> dir;
        private static final HashMap<Direction, Direction> OPPOSITE = new HashMap<>();

        static final TwoDirNode<Direction> northNode = new TwoDirNode<>(NORTH);
        static final TwoDirNode<Direction> southNode = new TwoDirNode<>(SOUTH);
        static final TwoDirNode<Direction> eastNode = new TwoDirNode<>(EAST);
        static final TwoDirNode<Direction> westNode = new TwoDirNode<>(WEST);

        static final HashMap<Direction, TwoDirNode<Direction>> DIRECTION_TO_NODE = new HashMap<>();

        static {
            OPPOSITE.put(NORTH, SOUTH);
            OPPOSITE.put(SOUTH, NORTH);
            OPPOSITE.put(EAST, WEST);
            OPPOSITE.put(WEST, EAST);

            northNode.left = westNode;
            northNode.right = eastNode;
            southNode.left = eastNode;
            southNode.right = westNode;
            westNode.right = northNode;
            westNode.left = southNode;
            eastNode.left = northNode;
            eastNode.right = southNode;

            DIRECTION_TO_NODE.put(NORTH, northNode);
            DIRECTION_TO_NODE.put(SOUTH, southNode);
            DIRECTION_TO_NODE.put(EAST, eastNode);
            DIRECTION_TO_NODE.put(WEST, westNode);
        }

        Direction(Pair<Integer> dir) {
            this.dir = dir;
        }

        Direction opposite() {
            return OPPOSITE.get(this);
        }
    }

    static class Move {
        Turn turn;
        int steps;

        public Move(Direction from, Direction to, int steps) {
            TwoDirNode<Direction> fromDirectionNode = Direction.DIRECTION_TO_NODE.get(from);
            TwoDirNode<Direction> toDirectionNode = Direction.DIRECTION_TO_NODE.get(to);
            turn = fromDirectionNode.left == toDirectionNode ? Turn.LEFT : Turn.RIGHT;
            this.steps = steps;
        }

        @Override
        public String toString() {
            return String.format("%s,%d",turn.code, steps);
        }
    }
}
