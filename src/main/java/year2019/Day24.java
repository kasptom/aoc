package year2019;

import aoc.IAocTask;
import year2019.utils.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Day24 implements IAocTask {
    static HashMap<Integer, Board> recursiveBoards = new HashMap<>();
    static int targetIterations = 200;
    static int RECURSIVE_BOARDS_DEPTH = targetIterations / 2 + 1;
    private static final Pair<Integer> RECURSIVE_CELL_POSITION = new Pair<>(2, 2);

    @Override
    public String getFileName() {
        return "aoc2019/input_24.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        String[][] input = loadBoard(lines);
        Board board = new Board(input);

        HashSet<Board> boards = new HashSet<>();
        while (!boards.contains(board)) {
            //System.out.println(board.getPrintout());
            boards.add(board);
            board = board.nextMinute();
        }
        System.out.println(board.hashCode());
    }

    /**
     * The scan you took (your puzzle input) shows where the bugs are on a single level of this structure.
     * The middle tile of your scan is empty to accommodate the recursive grids within it.
     * Initially, no other levels contain bugs
     */
    @Override
    public void solvePartTwo(List<String> lines) {
        String[][] input = loadBoard(lines);

        Board middleBoard = new Board(0, input);
        recursiveBoards.put(middleBoard.recursionLevelId, middleBoard);

        for (int i = 1; i <= RECURSIVE_BOARDS_DEPTH; i++) {
            Board upperBoard = new Board(-i, Board.empty());
            Board lowerBoard = new Board(i, Board.empty());
            recursiveBoards.put(upperBoard.recursionLevelId, upperBoard);
            recursiveBoards.put(lowerBoard.recursionLevelId, lowerBoard);
        }

//        printAllRecursiveBoards(0);
        for (int i = 0; i < targetIterations; i++) {
            Board board = recursiveBoards.get(0);
            board.nextMinuteRecursively();
//            printAllRecursiveBoards(i + 1);
        }
//        printAllRecursiveBoards(targetIterations);
        System.out.println(countBugsOnAllRecursionLevels());
    }

    @SuppressWarnings("unused")
    private void printAllRecursiveBoards(int iterations) {
        System.out.printf("%n ---- after %d iterations: ----%n", iterations);
        for (int i = -RECURSIVE_BOARDS_DEPTH; i <= RECURSIVE_BOARDS_DEPTH; i++) {
            System.out.println(recursiveBoards.get(i));
        }
        System.out.println("------------------------------");
    }

    private String[][] loadBoard(List<String> lines) {
        String[][] board = new String[lines.size()][lines.get(0).length()];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = lines.get(i).substring(j, j + 1);
            }
        }
        return board;
    }

    private int countBugsOnAllRecursionLevels() {
        return recursiveBoards
                .values()
                .stream()
                .map(board -> board.compressed)
                .map(board -> Arrays.stream(board).map(row -> Arrays.stream(row).sum()).reduce(Integer::sum).orElse(0))
                .reduce(Integer::sum)
                .orElse(0);
    }

    static class Board {
        private int recursionLevelId;
        static final int BOARD_SIZE = 5;
        private static final int[] DX = {0, 1, 0, -1};
        private static final int[] DY = {-1, 0, 1, 0};

        int[][] compressed = new int[BOARD_SIZE][BOARD_SIZE];

        public Board(String[][] board) {
            compress(board);
        }

        Board(int recursionLevelId, String[][] board) {
            this(board);
            this.recursionLevelId = recursionLevelId;
        }

        Board(int recursionLevelId, int[][] compressed) {
            this.recursionLevelId = recursionLevelId;
            this.compressed = compressed;
        }

        private Board(int[][] copy) {
            this.compressed = copy;
        }

        public static int[][] empty() {
            return new int[BOARD_SIZE][BOARD_SIZE];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Board)) return false;

            Board board = (Board) o;

            return board.hashCode() == o.hashCode();
        }

        @Override
        public int hashCode() {
            int hashCode = 0;
            int multiplier = 1;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE ; j++) {
                    hashCode += compressed[i][j] * multiplier;
                    multiplier = multiplier * 2;
                }
            }
            return hashCode;
        }

        @Override
        public String toString() {
            return getPrintout();
        }

        private void compress(String[][] board) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    compressed[i][j] = board[i][j].equals(".") ? 0 : 1;
                }
            }
        }

        public Board nextMinute() {
            int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    updateCell(copy, i, j);
                }
            }
            return new Board(copy);
        }

        private boolean hasBug(int i, int j) {
            return compressed[i][j] == 1;
        }

        private boolean hasBug2(int boardIdx, int i, int j) {
            return recursiveBoards.get(boardIdx).compressed[i][j] == 1;
        }

        private boolean isEmpty(int i, int j) {
            return compressed[i][j] == 0;
        }

        private boolean isEmpty2(int boardIdx, int i, int j) {
            return recursiveBoards.get(boardIdx).compressed[i][j] == 0;
        }

        private boolean isEmptyBoard() {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (compressed[i][j] == 1) return false;
                }
            }
            return true;
        }

        private int countNeighbours(int i, int j) {
            int counter = 0;
            for (int k = 0; k < 4; k++) {
                int neighbourI = i + DY[k];
                int neighbourJ = j + DX[k];

                if (isInRange(neighbourI, neighbourJ) && hasBug(neighbourI, neighbourJ)) {
                    counter++;
                }
            }
            return counter;
        }

        private int countNeighbours2(int boardIdx, int i, int j) {
            int counter = 0;
            for (int k = 0; k < 4; k++) {
                int neighbourI = i + DY[k];
                int neighbourJ = j + DX[k];

                if (isRecursiveCell(neighbourI, neighbourJ)) {
                    continue;
                }

                if (isInRange(neighbourI, neighbourJ) && hasBug2(boardIdx, neighbourI, neighbourJ)) {
                    counter++;
                }
            }
            return counter;
        }

        private int countLowerLayerNeighbours(int boardIdx, int i, int j) {
            Board lower = recursiveBoards.get(boardIdx + 1);

            int neighI, neighJ;
            for (int k = 0; k < 4; k++) {
                neighI = i + DY[k];
                neighJ = j + DX[k];

                /*
                 *        2
                 *  -|- - - - -|-
                 *   |A B C D E|
                 *   |F G H I J|
                 * 1 |K L ? N O| 3
                 *   |P Q R S T|
                 *   |U V W X Y|
                 *  -|- - - - -|-
                 *        0
                 *   0 1 2 ... - boardIdx     (this layer)
                 *   A B C ... - boardIdx + 1 (lower layer)
                 */
                if (isRecursiveCell(neighI, neighJ)) {
                    if (k == 2) {
                        return Arrays.stream(lower.compressed[0]).sum();
                    } else if (k == 0) {
                        return Arrays.stream(lower.compressed[BOARD_SIZE - 1]).sum();
                    } else if (k == 1) {
                        return sumColumn(lower.compressed, 0);
                    } else { // k == 3
                        return sumColumn(lower.compressed, BOARD_SIZE - 1);
                    }
                }
            }
            return 0;
        }

        private boolean isRecursiveCell(int i, int j) {
            return i == RECURSIVE_CELL_POSITION.y && j == RECURSIVE_CELL_POSITION.x;
        }

        private int countUpperLayerNeighbours(int boardIdx, int i, int j) {
            Board upper = recursiveBoards.get(boardIdx - 1);
            int neighboursCount = 0;

            int neighI, neighJ;
            for (int k = 0; k < 4; k++) {
                neighI = i + DY[k];
                neighJ = j + DX[k];

                /*
                 *        0
                 *  -|- - - - -|-
                 *   |A B C D E|
                 *   |F G H I J|
                 * 3 |K L ? N O| 1
                 *   |P Q R S T|
                 *   |U V W X Y|
                 *  -|- - - - -|-
                 *        2
                 *   0 1 2 ... - boardIdx - 1 (upper layer)
                 *   A B C ... - boardIdx     (this layer)
                 */
                if (isInRange(neighI, neighJ)) {
                    continue;
                }

                neighboursCount += upper.compressed[RECURSIVE_CELL_POSITION.y + DY[k]][RECURSIVE_CELL_POSITION.x + DX[k]];
            }
            return neighboursCount;
        }

        private int sumColumn(int[][] compressed, int columnIdx) {
            int sum = 0;
            for (int i = 0; i < BOARD_SIZE; i++) {
                sum += compressed[i][columnIdx];
            }
            return sum;
        }

        private boolean isInRange(int i, int j) {
            return i >= 0 && i < BOARD_SIZE && j >= 0 && j < BOARD_SIZE;
        }

        String getPrintout() {
            StringBuilder boardBuilder = new StringBuilder();
            boardBuilder.append(String.format("board: %d\n", recursionLevelId));

            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    boardBuilder.append(compressed[i][j] == 1 ? "#" : ".");
                }
                boardBuilder.append("\n");
            }

            return boardBuilder.toString();
        }

        public void nextMinuteRecursively() {
            if (recursionLevelId != 0) throw new RuntimeException("Can be called only from the initial board");
            HashMap<Integer, Board> nextMinuteBoards = new HashMap<>();
            nextMinuteBoards.put(-RECURSIVE_BOARDS_DEPTH, recursiveBoards.get(-RECURSIVE_BOARDS_DEPTH));
            nextMinuteBoards.put(RECURSIVE_BOARDS_DEPTH, recursiveBoards.get(RECURSIVE_BOARDS_DEPTH));

            for (int boardId = -RECURSIVE_BOARDS_DEPTH + 1; boardId <= RECURSIVE_BOARDS_DEPTH - 1; boardId++) {
                Board prevUpper = recursiveBoards.get(boardId - 1);
                Board prev = recursiveBoards.get(boardId);
                Board prevLower = recursiveBoards.get(boardId + 1);

                Board newBoard = new Board(prev.recursionLevelId, Board.empty());
                nextMinuteBoards.put(newBoard.recursionLevelId, newBoard);

                if (prevUpper.isEmptyBoard() && prev.isEmptyBoard() && prevLower.isEmptyBoard()) {
                    continue;
                }

                for (int i = 0; i < BOARD_SIZE; i++) {
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        if (isRecursiveCell(i, j)) {
                            newBoard.compressed[i][j] = 0;
                        } else {
                            newBoard.compressed[i][j] = getNewCellValue(newBoard.recursionLevelId, i, j);
                        }
                    }
                }
            }

            recursiveBoards = nextMinuteBoards;
        }

        private int getNewCellValue(int boardIdx, int i, int j) {
            int neighboursCount = countNeighbours2(boardIdx, i, j) + countLowerLayerNeighbours(boardIdx, i, j) + countUpperLayerNeighbours(boardIdx, i, j);
            if (hasBug2(boardIdx, i, j) && neighboursCount != 1) {
                return  0;
            } else if (isEmpty2(boardIdx, i, j) && (neighboursCount == 1 || neighboursCount == 2)) {
                return  1;
            } else {
                return recursiveBoards.get(boardIdx).compressed[i][j];
            }
        }

        private void updateCell(int[][] copy, int i, int j) {
            int neighboursCount = countNeighbours(i, j);
            if (hasBug(i, j) && neighboursCount != 1) {
                copy[i][j] = 0;
            } else if (isEmpty(i, j) && (neighboursCount == 1 || neighboursCount == 2)) {
                copy[i][j] = 1;
            } else {
                copy[i][j] = compressed[i][j];
            }
        }
    }
}
