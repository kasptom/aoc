package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

import static year2020.Direction.*;

public class Day12 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_12.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Action> actions = lines.stream().map(Action::parse).collect(Collectors.toList());
        State state = State.init();
        execute(state, actions);
        System.out.println(state.manhattan());
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Action> actions = lines.stream().map(Action::parse).collect(Collectors.toList());
        State state = State.init();
        execute2(state, actions);
        System.out.println(state.manhattan());
    }

    private void execute(State state, List<Action> actions) {
        for (Action action : actions) {
            execute(state, action);
//            System.out.print(action + " ");
//            state.printState();
        }
    }

    private void execute2(State state, List<Action> actions) {
        for (Action action : actions) {
            execute2(state, action);
//            System.out.print(action + " ");
//            state.printState();
        }
    }

    private void execute(State state, Action action) {
        if (ActionType.ROTATIONS.contains(action.type)) {
            state.rotate(action);
        } else if (ActionType.MOVES.contains(action.type)) {
            state.move(action);
        } else if (ActionType.SHIFTS.contains(action.type)) {
            state.shift(action);
        }
    }

    private void execute2(State state, Action action) {
        if (ActionType.ROTATIONS.contains(action.type)) {
            state.rotate2(action);
        } else if (ActionType.MOVES.contains(action.type)) {
            state.move2(action);
        } else if (ActionType.SHIFTS.contains(action.type)) {
            state.shift2(action);
        }
    }
}

class Action {
    final ActionType type;
    final int value;

    static Action parse(String line) {
        ActionType type = ActionType.valueOf(line.substring(0, 1));
        int value = Integer.parseInt(line.substring(1));
        if (ActionType.ROTATIONS.contains(type) && !Set.of(90, 180, 270).contains(value)) {
            throw new RuntimeException("wrong rotation");
        }
        return new Action(type, value);
    }

    private Action(ActionType type, int value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}

enum ActionType {
    N, S, E, W, L, R, F;
    static Set<ActionType> ROTATIONS = EnumSet.of(L, R);
    static Set<ActionType> SHIFTS = EnumSet.of(N, S, E, W);

    static Set<ActionType> MOVES = EnumSet.of(F);
    static Map<ActionType, int[]> MOVE_TO_XY_DIFF = Map.of(
            N, new int[]{0, 1},
            S, new int[]{0, -1},
            E, new int[]{1, 0},
            W, new int[]{-1, 0});
}

enum Direction {
    NORTH, SOUTH, EAST, WEST;
    static Map<Direction, List<Direction>> CLOCKWISE = Map.of(
            NORTH, List.of(EAST, SOUTH, WEST),
            EAST, List.of(SOUTH, WEST, NORTH),
            SOUTH, List.of(WEST, NORTH, EAST),
            WEST, List.of(NORTH, EAST, SOUTH));
    static Map<Direction, List<Direction>> COUNTER_CLOCK = Map.of(
            NORTH, List.of(WEST, SOUTH, EAST),
            EAST, List.of(NORTH, WEST, SOUTH),
            SOUTH, List.of(EAST, NORTH, WEST),
            WEST, List.of(SOUTH, EAST, NORTH));
    static Map<ActionType, Map<Direction, List<Direction>>> ROTATION_TO_MAP = Map.of(
            ActionType.L, COUNTER_CLOCK,
            ActionType.R, CLOCKWISE
    );
    static Map<Direction, int[]> STATE_ROTATION_TO_XY_DIFF = Map.of(
            NORTH, new int[]{0, 1},
            SOUTH, new int[]{0, -1},
            EAST, new int[]{1, 0},
            WEST, new int[]{-1, 0});
}

class State {
    int x, y;
    Direction rotation;
    int mileage;
    Waypoint waypoint;

    static State init() {
        var state = new State();
        state.x = 0;
        state.y = 0;
        state.rotation = EAST;
        state.mileage = 0;
        state.waypoint = new Waypoint(10, 1);
        return state;
    }

    public void rotate(Action action) {
        rotation = ROTATION_TO_MAP.get(action.type).get(rotation).get(action.value / 90 - 1);
    }

    public void rotate2(Action action) {
        waypoint.rotate(action);
    }

    void shift(Action shift) {
        int[] diff = ActionType.MOVE_TO_XY_DIFF.get(shift.type);
        x += diff[0] * shift.value;
        y += diff[1] * shift.value;
    }

    void shift2(Action shift) {
        int[] diff = ActionType.MOVE_TO_XY_DIFF.get(shift.type);
        waypoint.x += diff[0] * shift.value;
        waypoint.y += diff[1] * shift.value;
    }

    void move(Action move) {
        int[] diff = Direction.STATE_ROTATION_TO_XY_DIFF.get(rotation);
        x += diff[0] * move.value;
        y += diff[1] * move.value;
    }

    void move2(Action move) {
        x += move.value * waypoint.x;
        y += move.value * waypoint.y;
    }

    void printState() {
        System.out.format("(%3d, %3d) [%s] %n", x, y, rotation);
    }

    int manhattan() {
        printState();
        return Math.abs(x) + Math.abs(y);
    }
}

class Waypoint {
    int x;
    int y;

    public Waypoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rotate(Action action) {
        Direction first = x > 0 ? EAST : WEST;
        Direction second = y > 0 ? NORTH : SOUTH;
        first = ROTATION_TO_MAP.get(action.type).get(first).get(action.value / 90 - 1);
        int firstVal = Math.abs(x);
        second = ROTATION_TO_MAP.get(action.type).get(second).get(action.value / 90 - 1);
        int secondVal = Math.abs(y);

        update(first, firstVal);
        update(second, secondVal);
    }

    private void update(Direction direction, int val) {
        switch (direction) {
            case NORTH:
                y = val;
                break;
            case SOUTH:
                y = -val;
                break;
            case EAST:
                x = val;
                break;
            case WEST:
                x = -val;
                break;
        }
    }
}
