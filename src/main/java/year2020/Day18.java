package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

import static year2020.Task.TokenType.*;

public class Day18 implements IAocTask {

    @Override
    public String getFileName() {
        return "aoc2020/input_18.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Task> tasks = getTasks(lines);
        long sum = tasks.stream().map(Task::solve).reduce(Long::sum).orElse(-1L);
        System.out.println(sum);
    }

    private List<Task> getTasks(List<String> lines) {
        return lines.stream().map(Task::init)
                .collect(Collectors.toList());
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Task> tasks = getTasks(lines);
        long sum = tasks.stream().map(Task::solve2).reduce(Long::sum).orElse(-1L);
        System.out.println(sum);
    }
}

class Task {
    final String line;
    final List<Token> tokens;
    final LinkedList<Token> queue;

    public Task(String line, List<Token> tokens, LinkedList<Token> queue) {
        this.line = line;
        this.tokens = tokens;
        this.queue = queue;
    }

    static Task init(String input) {
        var parsed = Arrays.stream(input.replaceAll(" ", "").split("")).collect(Collectors.toList())
                .stream().map(Token::parse).collect(Collectors.toList());
        for (int idx = 1; idx <= parsed.size(); idx++) {
            parsed.get(idx - 1).id = idx;
        }
        return new Task(input, parsed, new LinkedList<>());
    }

    long solve() {
        System.out.println(line);
        queue.add(tokens.get(0));
        for (int i = 1; i < tokens.size(); i++) {
            System.out.println(printStack() + " | " + tokens.get(i));
            Token token = tokens.get(i);
            if (token.isMulOrAdd()) {
                queue.add(token);
            } else if (token.isValue()) {
                queue.add(token);
                boolean compressed = compressRightEnd(queue);
                while (compressed) {
                    compressed = compressRightEnd(queue);
                }
            } else if (token.isRPar()) {
                queue.add(token);
                boolean compressed = compressRightEnd(queue);
                while (compressed) {
                    compressed = compressRightEnd(queue);
                }
            } else if (token.isLPar()) {
                queue.add(token);
            }
        }
        boolean compressed = compressRightEnd(queue);
        while (compressed) {
            compressed = compressRightEnd(queue);
        }
        return queue.element().value;
    }

    private boolean compressRightEnd(LinkedList<Token> queue) {
        int startSize = queue.size();
        int lParIdx;
        for (lParIdx = queue.size() - 1; lParIdx >= 0; lParIdx--) {
            if (queue.get(lParIdx).isLPar()) {
                break;
            }
        }

        var lPar = lParIdx != -1 ? queue.get(lParIdx) : null;
        Token rPar = null;
        if (queue.get(queue.size() - 1).isRPar()) {
            rPar = queue.remove(queue.size() - 1);
        }
        for (int i = lParIdx + 1; i < queue.size(); i++) {
            var left = queue.get(i);
            if (i == queue.size() - 1) {
                break;
            }
            var op = queue.get(i + 1);
            var right = queue.get(i + 2);
            left.value = op.evaluate(left, right);
            queue.remove(op);
            queue.remove(right);
        }
        if (rPar != null) {
            queue.remove(lPar);
        }
        return startSize != queue.size();
    }

    String printStack() {
        return new ArrayList<>(queue).toString();
    }

    public long solve2() {
        System.out.println(line);
        for (var token : tokens) {
            queue.add(token);
            boolean queueSizeDecreased = true;
            System.out.println(printStack() + " | " + token);
            while (queueSizeDecreased) {
                int startSize = queue.size();
                var top = queue.getLast();
                if (top.isValue()) {
                    boolean can = true;
                    while (can) {
                        can = canExecuteAdd(queue);
                    }
                } else if (top.isRPar()) {
                    compressMulls(queue);
                }
                queueSizeDecreased = startSize != queue.size();
            }
        }
        System.out.println("all tokens processed");
        boolean queueSizeDecreased = true;
        while (queueSizeDecreased) {
            System.out.println(printStack());
            int startSize = queue.size();
            var top = queue.getLast();
            if (top.isValue()) {
                boolean can = true;
                while (can) {
                    can = canExecuteAdd(queue);
                }
            }
            compressMullsNoParen(queue);
            queueSizeDecreased = startSize != queue.size();
        }

        return queue.get(0).value;
    }

    private void compressMullsNoParen(LinkedList<Token> queue) {
        long product = 1L;
        for (int i = 0; i < queue.size(); i += 2) {
            product *= queue.get(i).value;
        }
        queue.clear();
        queue.add(new Token(product, VAL));
    }

    private void compressMulls(LinkedList<Token> queue) {
        int lParIdx = findLastLPar(queue);
        long mulResult = 1;
        List<Token> toRemove = queue.subList(lParIdx, queue.size());
        System.out.println("-- to remove: " + toRemove);
        if (toRemove.size() == 3) {
            mulResult = toRemove.get(1).value;
        } else {
            for (int i = 1; i < toRemove.size() - 1; i += 2) {
                mulResult *= toRemove.get(i).value;
            }
        }
        int size = toRemove.size();
        for (int i = 1; i <= size; i++) {
            queue.removeLast();
        }
        System.out.println("-- replaced with: " + mulResult);
        queue.add(new Token(mulResult, VAL));
        System.out.println(printStack());
    }

    private int findLastLPar(LinkedList<Token> queue) {
        for (int i = queue.size() - 1; i >= 0; i--) {
            if (queue.get(i).isLPar()) {
                return i;
            }
        }
        throw new RuntimeException("( not found");
    }

    private boolean canExecuteAdd(LinkedList<Token> queue) {
        if (queue.size() < 3) return false;
        if (!queue.get(queue.size() - 2).isAdd()) return false;
        var right = queue.removeLast();
        var op = queue.removeLast();
        var left = queue.removeLast();
        left.value = op.evaluate(left, right);
        queue.addLast(left);
        return true;
    }

    static class Token {
        int id;
        Long value;
        TokenType type;

        public Token(Long value, TokenType type) {
            this.value = value;
            this.type = type;
        }

        static Token parse(String token) {
            TokenType type = TokenType.parse(token);
            Long value = null;
            if (type.isValue()) {
                value = Long.parseLong(token);
            }
            return new Token(value, type);
        }

        boolean isValue() {
            return !this.type.isOperation();
        }

        boolean isAdd() {
            return type.equals(ADD);
        }

        boolean isMulOrAdd() {
            return this.type.equals(MUL) || this.type.equals(ADD);
        }

        boolean isLPar() {
            return this.type.equals(L_PAR);
        }

        boolean isRPar() {
            return this.type.equals(R_PAR);
        }

        long evaluate(Token a, Token b) {
            if (!a.isValue() || !b.isValue()) {
                throw new RuntimeException("Not value");
            }
            if (!this.isMulOrAdd()) {
                throw new RuntimeException("Not operation");
            }
            if (this.type.equals(MUL)) {
                return a.value * b.value;
            } else {
                return a.value + b.value;
            }
        }

        @Override
        public String toString() {
            return type.isOperation() ? type.toString() : value.toString();
        }

        @Override
        public boolean equals(Object token) {
            if (!(token instanceof Token)) return false;
            return id == ((Token) token).id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    enum TokenType {
        ADD, MUL, L_PAR, R_PAR, VAL;
        static Map<String, TokenType> STR_TO_TOKEN_TYPE = Map.of("(", L_PAR, ")", R_PAR, "X", VAL, "*", MUL, "+", ADD);
        static Map<TokenType, String> TOKEN_TYPE_TO_STR = Map.of(L_PAR, "(", R_PAR, ")", VAL, "X", MUL, "*", ADD, "+");
        static EnumSet<TokenType> OPS = EnumSet.range(ADD, R_PAR);

        static TokenType parse(String input) {
            String token = input.matches("\\d+") ? "X" : input;
            return STR_TO_TOKEN_TYPE.get(token);
        }

        boolean isValue() {
            return this.equals(VAL);
        }

        boolean isOperation() {
            return OPS.contains(this);
        }

        @Override
        public String toString() {
            return TOKEN_TYPE_TO_STR.get(this);
        }
    }
}
