package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Day13 implements IAocTask {
    String[][] gameBoard = new String[50][50];
    final long[] maxX = {0};
    final long[] maxY = {0};

    final long[] ballX = {0};
    final long[] paddleX = {0};

    @Override
    public String getFileName() {
        return "aoc2019/input_13.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        long[] program = Aoc2019Utils.loadProgram(lines);
        program = Aoc2019Utils.copyToLargerMemory(program, 6000);

        final int[] outputCounter = {0};
        final int[] blockCounter = {0};
        final long[] x = {0};
        final long[] y = {0};

        Day05 intComp = new Day05();
        intComp.setIoListeners(() -> {
            return 1;
        }, (output, instructionPointer) -> {
            outputCounter[0]++;
            int counter = outputCounter[0];
            if (counter % 3 == 1) {
                x[0] = output;
            } else if (counter % 3 == 2) {
                y[0] = output;
            }

            if (counter % 3 == 0) {
                printTileInfo(output, x[0], y[0]);

                if (output == TileType.BLOCK) {
                    blockCounter[0]++;
                }

                if (x[0] == -1 && y[0] == 0) {
                    System.out.printf("score %d%n", output);
                } else {
                    maxX[0] = Math.max(x[0], maxX[0]);
                    maxY[0] = Math.max(y[0], maxY[0]);
                }
            }
            return false;
        }, () -> System.out.println(blockCounter[0]));

        intComp.runProgram(program, new long[2]);
        printGameBoard();
    }

    private void printGameBoard() {
        for (int i = 0; i <= maxY[0]; i++) {
            for (int j = 0; j <= maxX[0]; j++) {
                System.out.print(gameBoard[i][j]);
            }
            System.out.println();
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        long[] program = Aoc2019Utils.loadProgram(lines);
        program = Aoc2019Utils.copyToLargerMemory(program, 6000);
        setFreeGameMode(program);

        final int[] outputCounter = {0};
        final long[] x = {0};
        final long[] y = {0};

        HashMap<Integer, Integer> controlToMove = new HashMap<>();
        controlToMove.put(8, -1);
        controlToMove.put(9, 0);
        controlToMove.put(0, 1);
        final long[] score = {-1};

        Scanner scanner = new Scanner(System.in);
        Day05 intComp = new Day05();
        intComp.setIoListeners(() -> {
//            clearConsole();
            System.out.println("-- #AOC2019 -- Breakout -- @kasptom --");
            System.out.printf("SCORE: %5d%n", score[0]);
            printGameBoard();
            //Thread.sleep(5);
            findBallAndTile();
            System.out.println();
//            System.out.println("input 8 9 0 to: move left, stay, move right");
//            int input = scanner.nextInt();
//            int finalInput = input;
//            while (controlToMove.keySet().stream().noneMatch(control -> control.equals(finalInput))) {
//                input = scanner.nextInt();
//                scanner.nextLine();
//            }
//            return (long) controlToMove.get(input);
            return Long.compare(ballX[0], paddleX[0]);
        }, (output, instrPointer) -> {
            outputCounter[0]++;
            int counter = outputCounter[0];
            if (counter % 3 == 1) {
                x[0] = output;
            } else if (counter % 3 == 2) {
                y[0] = output;
            }

            if (counter % 3 == 0) {
                if (x[0] == -1 && y[0] == 0) {
                    score[0] = output;
                } else {
                    printTileInfo(output, x[0], y[0]);
                    maxX[0] = Math.max(x[0], maxX[0]);
                    maxY[0] = Math.max(y[0], maxY[0]);
                }
            }
            return false;
        }, () -> System.out.println(score[0]));

        intComp.runProgram(program, new long[2]);
    }

    private void findBallAndTile() {
        for (int i = 0; i <= maxX[0]; i++) {
            for (int j = 0; j <= maxY[0]; j++) {
                if (gameBoard[j][i].equals(TileType.IC_BALL)) {
                    ballX[0] = i;
                }
                if (gameBoard[j][i].equals(TileType.IC_HORIZONTAL_PADDLE)) {
                    paddleX[0] = i;
                }
            }
        }
    }

    private void setFreeGameMode(long[] program) {
        program[0] = 2;
    }

    private void printTileInfo(long output, long x, long y) {
        String elementName = getObjectName(output);
        updateBoard(output, x, y);
//        printTileInfo(x, y, elementName);
    }

    public void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void updateBoard(long output, long x, long y) {
        gameBoard[(int) y][(int) x] = getObjectSymbol(output);
    }

    private String getObjectName(long output) {
        switch ((int) output) {
            case TileType.EMPTY:
                return "EMPTY";
            case TileType.WALL:
                return "WALL";
            case TileType.BLOCK:
                return "BLOCK";
            case TileType.BALL:
                return "BALL";
            case TileType.HORIZONTAL_PADDLE:
                return "PADDLE";
            default:
                return "UNKNOWN";
        }
    }

    private String getObjectSymbol(long output) {
        switch ((int) output) {
            case TileType.EMPTY:
                return TileType.IC_EMPTY;
            case TileType.WALL:
                return TileType.IC_WALL;
            case TileType.BLOCK:
                return TileType.IC_BLOCK;
            case TileType.BALL:
                return TileType.IC_BALL;
            case TileType.HORIZONTAL_PADDLE:
                return TileType.IC_HORIZONTAL_PADDLE;
            default:
                return "X";
        }
    }

    private void printTileInfo(long x, long l, String elementName) {
        System.out.printf("%s at (x, y)= (%2d, %2d)%n", elementName, x, l);
    }

    static class TileType {
        static final int EMPTY = 0;
        static final int WALL = 1;
        static final int BLOCK = 2;
        static final int HORIZONTAL_PADDLE = 3;
        static final int BALL = 4;

        static final String IC_EMPTY = "░";
        static final String IC_WALL = "█";
        static final String IC_BLOCK = "▓";
        static final String IC_HORIZONTAL_PADDLE = "─";
        static final String IC_BALL = "o";
    }
}
