package year2019;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22 implements IAocTask {
    private List<Integer> cards;
    String SHF_STACK = "deal into new stack";
    String SHF_INC_DEAL = "deal with increment";
    String SHF_CUT = "cut";

    @Override
    public String getFileName() {
        return "aoc2019/input_21.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<CardShuffle> shuffles = getShuffles(lines);
        cards = IntStream.range(0, 10007).boxed().collect(Collectors.toCollection(ArrayList::new));
        shuffles.forEach(this::executeShuffle);
        System.out.println(cards.indexOf(2019));
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<CardShuffle> shuffles = getShuffles(lines);
        cards = IntStream.range(0, 10007).boxed().collect(Collectors.toCollection(ArrayList::new));
        List<Integer> originalOrder = new ArrayList<>(cards);

        shuffles.forEach(this::executeShuffle);
        int iterations = 1;
        while (!originalOrder.equals(cards)) {
            List<Integer> prevShuffled = new ArrayList<>(cards);
            //System.out.println(cards.get(2020));
            shuffles.forEach(this::executeShuffle);
            printPositionChanges(prevShuffled);
            iterations++;
        }

        System.out.printf("cycle after: %d iterations%n", iterations);


//        System.out.printf("card at 2020th position: %d%n", cards.get(2020));
//
        long newCardsCount = 119315717514047L;
        long repeats = 101741582076661L;


        /*
        System.out.printf("Shuffles after cycle %d%n", newCardsCount % repeats);
        long start = System.nanoTime();
        System.out.println("start");
         TAKES TOO LONG:
        for (long i = 0; i < newCardsCount % repeats; i++) {
        }
        System.out.printf("done %.3f [s]%n", (System.nanoTime() - start) / 1e9);
         */
    }

    private void printPositionChanges(List<Integer> prevShuffled) {
        int prevIdx = prevShuffled.indexOf(cards.get(2020));
        System.out.printf("Card %d on position 2020 was previously on %d position%n", cards.get(2020), prevIdx);
    }

    private void executeShuffle(CardShuffle shuffle) {
        if (shuffle.name.equals(SHF_STACK)) {
            cards = invertList(cards);
        } else if (shuffle.name.equals(SHF_INC_DEAL)) {
            cards = incDeal(cards, shuffle.value);
        } else {
            cards = cutCards(cards, shuffle.value);
        }
    }

    protected List<Integer> incDeal(List<Integer> cards, int increment) {
        List<Integer> shuffled = new ArrayList<>(cards);
        int checksum = cards.stream().reduce(Integer::sum).orElse(-1);
        int newChecksum;
        int shuffleIdx = 0;

        for (int i = 0; i < cards.size(); i++) {
            shuffled.set(shuffleIdx, cards.get(i));
            shuffleIdx = shuffleIdx + increment;
            if (shuffleIdx >= cards.size()) {
                shuffleIdx = shuffleIdx - cards.size();
            }
            //System.out.printf("shuffle idx: %d%n", shuffleIdx);
        }

        newChecksum = shuffled.stream().reduce(Integer::sum).orElse(-1);
        if (newChecksum != checksum) throw new RuntimeException("invalid checksum");

        return shuffled;
    }

    protected List<Integer> cutCards(List<Integer> cards, int value) {
        List<Integer> cut = new ArrayList<>();
        if (value > 0) {
            cut = cards.subList(0, value);
            cards = cards.subList(value, cards.size());
            cards.addAll(cut);
        } else {
            cut = cards.subList(cards.size() + value, cards.size());
            cut.addAll(cards.subList(0, cards.size() + value));
            cards = cut;
        }
        return cards;
    }

    protected List<Integer> invertList(List<Integer> cards) {
        List<Integer> inverted = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            inverted.add(cards.get(cards.size() - i - 1));
        }
        return inverted;
    }


    private List<CardShuffle> getShuffles(List<String> lines) {
        List<CardShuffle> shuffles;
        shuffles = lines.stream().map(this::mapToShuffle).collect(Collectors.toCollection(ArrayList::new));
        return shuffles;
    }

    private CardShuffle mapToShuffle(String line) {
        if (line.startsWith(SHF_STACK)) {
            return new CardShuffle(SHF_STACK, -1);
        } else if (line.startsWith(SHF_CUT)) {
            return new CardShuffle(SHF_CUT, Integer.parseInt(line.replace(SHF_CUT, "").trim()));
        } else {
            return new CardShuffle(SHF_INC_DEAL, Integer.parseInt(line.replace(SHF_INC_DEAL, "").trim()));
        }
    }

    static class CardShuffle {
        String name;
        int value;

        public CardShuffle(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
