package year2020;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day18TaskTest {
    @Test
    public void solve1_1() {
        String input = "1 + 2 * 3 + 4 * 5 + 6";
        Task task = Task.init(input);

        assertEquals(71L, task.solve());
    }

    @Test
    public void solve1_2() {
        String input = "1 + (2 * 3) + (4 * (5 + 6))";
        Task task = Task.init(input);

        assertEquals(51, task.solve());
    }

    @Test
    public void solve1_3() {
        String input = "2 * 3 + (4 * 5)";
        Task task = Task.init(input);

        assertEquals(26, task.solve());
    }

    @Test
    public void solve1_4() {
        String input = "5 + (8 * 3 + 9 + 3 * 4 * 3)";
        Task task = Task.init(input);

        assertEquals(437, task.solve());
    }

    @Test
    public void solve1_5() {
        String input = "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))";
        Task task = Task.init(input);

        assertEquals(12240, task.solve());
    }

    @Test
    public void solve1_6() {
        String input = "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2";
        Task task = Task.init(input);

        assertEquals(13632, task.solve());
    }

    @Test
    public void solve2_1() {
        String input = "1 + (2 * 3) + (4 * (5 + 6))";
        Task task = Task.init(input);

        assertEquals(51, task.solve2());
    }

    @Test
    public void solve2_2() {
        String input = "2 * 3 + (4 * 5)";
        Task task = Task.init(input);

        assertEquals(46, task.solve2());
    }

    @Test
    public void solve2_3() {
        String input = "5 + (8 * 3 + 9 + 3 * 4 * 3)";
        Task task = Task.init(input);

        assertEquals(1445, task.solve2());
    }

    @Test
    public void solve2_4() {
        String input = "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))";
        Task task = Task.init(input);

        assertEquals(669060, task.solve2());
    }

    @Test
    public void solve2_5() {
        String input = "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2";
        Task task = Task.init(input);

        assertEquals(23340, task.solve2());
    }
}
