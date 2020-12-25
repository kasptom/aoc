package year2020;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static year2020.InputType.MASK;
import static year2020.InputType.MEM;

public class Day14 implements IAocTask {

    @Override
    public String getFileName() {
        return "aoc2020/input_14.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Input> inputs = lines.stream().map(Input::parse)
                .collect(Collectors.toList());
        long sum = computeSum(inputs);
        System.out.println(sum);
    }

    private long computeSum(List<Input> inputs) {
        HashMap<Integer, Long> memory = new HashMap<>();
        String currentMask = null;
        for (var input : inputs) {
            if (input.type.equals(MASK)) {
                currentMask = input.value;
            } else { // MEM
                if (currentMask == null) throw new RuntimeException("wrong state");
                var value = putMask(currentMask, input);
                memory.put(input.idx, value);
            }
        }
        return memory.values().stream().mapToLong(Long::longValue).sum();
    }

    private long putMask(String currentMask, Input input) {
        String[] newValue = String.format("%36s", input.value).replace(' ', '0').split("");
        String[] mask = currentMask.split("");
        for (int i = 0; i < currentMask.length(); i++) {
            if (!mask[i].equals("X")) {
                newValue[i] = mask[i];
            }
        }
        return Long.parseLong(String.join("", newValue), 2);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        List<Input> inputs = lines.stream().map(Input::parse)
                .collect(Collectors.toList());
        long sum = computeSumV2(inputs);
        System.out.println(sum);
    }

    private long computeSumV2(List<Input> inputs) {
        HashMap<Long, Long> memory = new HashMap<>();
        String currentMask = null;
        for (var input : inputs) {
            if (input.type.equals(MASK)) {
                currentMask = input.value;
            } else { // MEM
                if (currentMask == null) throw new RuntimeException("wrong state");
                updateMemory(memory, currentMask, input);
            }
        }
        return memory.values().stream().mapToLong(Long::longValue).sum();
    }

    private void updateMemory(HashMap<Long, Long> memory, String currentMask, Input input) {
        String[] originalMemoryAddress = String.format("%36s", Integer.toBinaryString(input.idx)).replace(' ', '0').split("");
        String[] mask = currentMask.split("");
        List<Long> addresses = generateAddresses(originalMemoryAddress, mask);
        addresses.forEach(address -> memory.put(address, Long.parseLong(input.value, 2)));
    }

    private List<Long> generateAddresses(String[] address, String[] mask) {
        List<String> addresses = new ArrayList<>();
        if (mask[0].equals("X")) {
            address[0] = "0";
            generateAddresses(addresses, address, mask, 1);
            address[0] = "1";
            generateAddresses(addresses, address, mask, 1);
        } else if (mask[0].equals("1")) {
            address[0] = "1";
            generateAddresses(addresses, address, mask, 1);
        } else {
            generateAddresses(addresses, address, mask, 1);
        }
        return addresses.stream().map(addr -> Long.parseLong(addr, 2)).collect(Collectors.toList());
    }

    private void generateAddresses(List<String> addresses, String[] address, String[] mask, int idx) {
        if (idx == mask.length) {
//            System.out.println("SAVING: " + String.join("", address).substring(0, idx));
            var value = String.join("", address);
            addresses.add(value);
            return;
        }

        if (mask[idx].equals("X")) {
            address[idx] = "0";
            generateAddresses(addresses, address, mask, idx + 1);
            address[idx] = "1";
            generateAddresses(addresses, address, mask, idx + 1);
        } else if (mask[idx].equals("1")) {
            address[idx] = "1";
            generateAddresses(addresses, address, mask, idx + 1);
        } else {
            generateAddresses(addresses, address, mask, idx + 1);
        }
    }
}

class Input {
    final InputType type;
    final String value;
    int idx;

    public Input(InputType type, String value, int idx) {
        this.type = type;
        this.value = value;
        this.idx = idx;
    }

    static Input parse(String line) {
        String[] inputStr = line.split(" = ");
        var type = inputStr[0].startsWith("mask") ? MASK : MEM;
        var value = type == MASK ? inputStr[1] : Integer.toBinaryString(Integer.parseInt(inputStr[1]));
        int idx = 0;
        if (type == MEM) {
            idx = Integer.parseInt(inputStr[0].replace("mem[", "").replace("]", ""));
        }
        return new Input(type, value, idx);
    }
}

enum InputType {
    MASK, MEM
}
