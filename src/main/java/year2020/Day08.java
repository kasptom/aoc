package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day08 implements IAocTask {
    @Override
    public String getFileName() {
        return "aoc2020/input_08.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Operation> operations = lines.stream().map(Operation::parse)
                .collect(Collectors.toList());
        int[] acc = {0};
        execute(operations, acc, 0);
    }

    private void execute(List<Operation> operations, int[] acc, int pointer) {
        var operation = operations.get(pointer);
        if (operation.executed) {
            System.out.println(acc[0]);
            return;
        }
        operation.executed = true;
        switch (operation.code) {
            case ACC:
                acc[0] += operation.arg;
                pointer++;
                execute(operations, acc, pointer);
                break;
            case JMP:
                pointer += operation.arg;
                execute(operations, acc, pointer);
                break;
            case NOP:
                pointer += 1;
                execute(operations, acc, pointer);
                break;
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Operation> operations = lines.stream().map(Operation::parse)
                .collect(Collectors.toList());
        int[] acc = {0};
        for (int j = 0; j < operations.size(); j++) {
            acc[0] = 0;
            operations.forEach(operation -> operation.executed = false);
            var operation = operations.get(j);
            var prevCode = operation.code;
            if (prevCode == OpCode.JMP) {
                operation.code = OpCode.NOP;
            } else if (prevCode == OpCode.NOP) {
                operation.code = OpCode.JMP;
            } else {
                continue;
            }
            if (execute2(operations, acc, 0)) {
                break;
            }
            operation.code = prevCode;
        }
    }

    private boolean execute2(List<Operation> operations, int[] acc, int pointer) {
        if (pointer >= operations.size()) {
            System.out.format("terminated!!! %d", acc[0]);
            return true;
        }

        var operation = operations.get(pointer);
        if (operation.executed) {
//            System.out.println(acc[0]);
            return false;
        }

        operation.executed = true;
        switch (operation.code) {
            case ACC:
                acc[0] += operation.arg;
                pointer++;
                return execute2(operations, acc, pointer);
            case JMP:
                pointer += operation.arg;
                return execute2(operations, acc, pointer);
            case NOP:
                pointer += 1;
                return execute2(operations, acc, pointer);
        }
        return false;
    }
}

class Operation {
    OpCode code;
    int arg;
    boolean executed;

    public static Operation parse(String line) {
        var operation = new Operation();
        String[] codeAndArg = line.split(" ");
        operation.code = OpCode.valueOf(codeAndArg[0].toUpperCase());
        operation.arg = Integer.parseInt(codeAndArg[1]);
        return operation;
    }
}

enum OpCode {
    ACC, JMP, NOP
}
