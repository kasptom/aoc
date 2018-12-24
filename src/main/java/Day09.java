import org.magicwerk.brownies.collections.GapList;

import java.util.Arrays;
import java.util.List;

public class Day09 implements IAocTask {
    @Override
    public String getFileName() {
        return "input_09_simple.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        String[] playersLastMarbleValue = lines.get(0).split(";");
        int players = Integer.parseInt(playersLastMarbleValue[0].trim());
        int marbleValue = Integer.parseInt(playersLastMarbleValue[1].trim());

        long bestScore = findBestScore(players, marbleValue);
        System.out.println(bestScore);
    }

    private long findBestScore(int players, int marbleValue) {
        long[] playersScores = new long[players];
        List<Integer> board = new GapList<>();
        board.add(0);
//        printBoard(board, 0);

        int currentMarbleValue = 1;
        int removedMarbleValue;
        int boardIdx = 0;
        int playerIdx = 0;

        while (currentMarbleValue <= marbleValue) {

            if (currentMarbleValue % 23 == 0) {

                boardIdx = move(board, boardIdx, -7);

                removedMarbleValue = board.remove(boardIdx); // marble is removed

                boardIdx = boardIdx % board.size();

                playersScores[playerIdx] += removedMarbleValue;
                playersScores[playerIdx] += currentMarbleValue;

            } else {
                boardIdx = move(board, boardIdx, 2);
                board.add(boardIdx, currentMarbleValue);
            }

            playerIdx = (playerIdx + 1) % players;
            currentMarbleValue++;
//            printBoard(board, boardIdx);
        }

        return Arrays.stream(playersScores).max().orElse(-1);
    }

//    private void printBoard(List<Integer> board, int boardIdx) {
//        for (int i = 0; i < board.size(); i++) {
//            if (boardIdx == i) {
//                System.out.printf("(%d) ", board.get(i));
//            } else {
//                System.out.printf("%d ", board.get(i));
//            }
//        }
//        System.out.println();
//    }

    private int move(List<Integer> board, int boardIdx, int move) {
        if (board.isEmpty()) {
            return 0;
        }

        int currentSize = board.size();

        int idx = (boardIdx + move) % currentSize;
        return idx < 0 ? (currentSize + idx) : idx;
    }


    @Override
    public void solvePartTwo(List<String> lines) {

    }
}
