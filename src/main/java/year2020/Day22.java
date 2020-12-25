package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day22 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_22.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        int playerSeparatorIdx = lines.indexOf("");
        List<Integer> firstPlayer = createPlayer(lines, playerSeparatorIdx, 1);
        List<Integer> secondPlayer = createPlayer(lines, lines.size(), playerSeparatorIdx + 2);
        while (!firstPlayer.isEmpty() && !secondPlayer.isEmpty()) {
            playRound(firstPlayer, secondPlayer);
        }
        long winnerScore = calculateWinnerScore(firstPlayer.isEmpty() ? secondPlayer : firstPlayer);
        System.out.println(winnerScore);
    }

    private void playRound(List<Integer> firstPlayer, List<Integer> secondPlayer) {
        Integer first = firstPlayer.remove(0);
        Integer second = secondPlayer.remove(0);
        List<Integer> drawn = new ArrayList<>(List.of(first, second)).stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        if (first > second) {
            firstPlayer.addAll(drawn);
        } else {
            secondPlayer.addAll(drawn);
        }
//        System.out.println(firstPlayer);
//        System.out.println(secondPlayer);
    }

    private long calculateWinnerScore(List<Integer> integers) {
        int deckSize = integers.size();
        long score = 0;
        for (int i = 0; i < integers.size(); i++) {
            score += integers.get(deckSize - 1 - i) * (i + 1);
        }
        return score;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int playerSeparatorIdx = lines.indexOf("");
        List<Integer> firstPlayer = createPlayer(lines, playerSeparatorIdx, 1);
        List<Integer> secondPlayer = createPlayer(lines, lines.size(), playerSeparatorIdx + 2);
        Set<String> firstSnapshot = new HashSet<>();
        Set<String> secondSnapshot = new HashSet<>();
        int winner = playRecursive(firstPlayer, secondPlayer, firstSnapshot, secondSnapshot, 1, 1);
//        System.out.println("The winner of game 1 id player " + winner);
        long winnerScore = calculateWinnerScore(winner != 2 ? firstPlayer : secondPlayer);
        System.out.println(winnerScore);
    }

    private List<Integer> createPlayer(List<String> lines, int playerSeparatorIdx, int i) {
        return lines.subList(i, playerSeparatorIdx).stream().map(Integer::valueOf).collect(Collectors.toList());
    }

    /**
     * Recursive Combat still starts by splitting the cards into two decks (you offer to play with the same starting
     * decks as before - it's only fair). Then, the game consists of a series of rounds with a few changes:
     * <p>
     * - Before either player deals a card, if there was a previous round in this game that had exactly the same cards in
     * the same order in the same players' decks, the game instantly ends in a win for player 1. Previous rounds from
     * other games are not considered. (This prevents infinite games of Recursive Combat, which everyone agrees is a
     * bad idea.)
     * - Otherwise, this round's cards must be in a new configuration; the players begin the round by each drawing the top
     * card of their deck as normal.
     * - If both players have at least as many cards remaining in their deck as the value of the card they just drew, the
     * winner of the round is determined by playing a new game of Recursive Combat (see below).
     * - Otherwise, at least one player must not have enough cards left in their deck to recurse; the winner of the round
     * is the player with the higher-value card.
     */
    private int playRecursive(List<Integer> firstPlayer, List<Integer> secondPlayer, Set<String> firstSnapshot, Set<String> secondSnapshot, int gameName, int roundName) {
        if (firstPlayer.isEmpty() || secondPlayer.isEmpty()) {
            return firstPlayer.isEmpty() ? 2 : 1;
        }
        // first rule
//        System.out.println("snapshot: " + snapshot);
        String snapOne = createSnapshot(firstPlayer);
        String snapTwo = createSnapshot(secondPlayer);
//        System.out.format("=== Game %s ===%n", gameName);
//        System.out.format("-- Round %d (Game %d) --%n", roundName, gameName);
//        System.out.println("Player 1's deck" + firstPlayer);
//        System.out.println("Player 2's deck" + secondPlayer);
        if (firstSnapshot.contains(snapOne) || secondSnapshot.contains(snapTwo)) {
            return 1;
        } else {
            firstSnapshot.add(snapOne);
            secondSnapshot.add(snapTwo);
        }

        // second rule
        Integer first = firstPlayer.remove(0);
        Integer second = secondPlayer.remove(0);
//        System.out.println("Player 1 plays: " + first);
//        System.out.println("Player 2 plays: " + second);

        // third rule
        int roundWinner;
        if (firstPlayer.size() >= first && secondPlayer.size() >= second) {
            List<Integer> copyOne = new ArrayList<>(firstPlayer.subList(0, first));
            List<Integer> copyTwo = new ArrayList<>(secondPlayer.subList(0, second));
            Set<String> newSnapshotOne = new HashSet<>();
            Set<String> newSnapshotTwo = new HashSet<>();
//            System.out.println("Playing a sub-game to determine the winner...");
            roundWinner = playRecursive(copyOne, copyTwo, newSnapshotOne, newSnapshotTwo, gameName + 1, 1);
//            System.out.println("...anyway, back to game " + gameName + " round " + roundName);
        } else {
            roundWinner = first > second ? 1 : 2;
        }
        if (roundWinner == 1) {
            List<Integer> drawn = List.of(first, second);
            firstPlayer.addAll(drawn);
        } else if (roundWinner == 2) {
            List<Integer> drawn = List.of(second, first);
            secondPlayer.addAll(drawn);
        }
//        System.out.format("Player %d wins round %d of game %d%n%n", roundWinner != 2 ? 1 : 2, roundName, gameName);
        return playRecursive(firstPlayer, secondPlayer, firstSnapshot, secondSnapshot, gameName, roundName + 1);
    }

    private String createSnapshot(List<Integer> deck) {
        return deck.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
