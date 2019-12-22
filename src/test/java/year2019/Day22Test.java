package year2019;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22Test {

    @Test
    public void tenCards_dealWithIncrement_correctOrder() {
        List<Integer> cards = IntStream.range(0, 11).boxed().collect(Collectors.toCollection(ArrayList::new));

        Day22 day = new Day22();
        cards = day.incDeal(cards, 3);

        System.out.println(cards);
    }

    @Test
    public void tenCards_invert_invertedOrder() {
        List<Integer> cards = IntStream.range(0, 11).boxed().collect(Collectors.toCollection(ArrayList::new));

        Day22 day = new Day22();
        cards = day.invertList(cards);

        System.out.println(cards);
    }

    @Test
    public void tenCards_cutA_correctOrder() {
        List<Integer> cards = IntStream.range(0, 11).boxed().collect(Collectors.toCollection(ArrayList::new));

        Day22 day = new Day22();
        cards = day.cutCards(cards, -3);

        System.out.println(cards);
    }

    @Test
    public void tenCards_cutB_correctOrder() {
        List<Integer> cards = IntStream.range(0, 11).boxed().collect(Collectors.toCollection(ArrayList::new));

        Day22 day = new Day22();
        cards = day.cutCards(cards, 3);

        System.out.println(cards);
    }
}
