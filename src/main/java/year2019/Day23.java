package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Day23 implements IAocTask {
    private static final int PACKET_LENGTH = 3;
    private static final int IDX_ADDRESS = 0;
    private static final int IDX_X = 1;
    private static final int IDX_Y = 2;

    /**
     * Address of the NAT - Not Always Transmitting device
     */
    private static final int NAT_ADDRESS = 255;

    final boolean[] isValueFor255Set = {false};
    long[] lastPacketForNat = new long[3];

    private static final int MAX_AVAILABLE_ADDRESS = 49;

    private HashMap<Long, Queue<long[]>> packetsQueue;
    TreeMap<Integer, NicState> computerStates = new TreeMap<>();

    @Override
    public String getFileName() {
        return "aoc2019/input_23.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        packetsQueue = initializePacketsQueue();
        runNetwork(lines);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        packetsQueue = initializePacketsQueue();
        long lastPacketValue = 0;
        while (true) {
            isValueFor255Set[0] = false;
            runNetwork(lines);
            if (isNetworkIdle()) {
                long[] natPacket = new long[PACKET_LENGTH];
                System.arraycopy(lastPacketForNat, 0, natPacket, 0, PACKET_LENGTH);
                if (lastPacketValue == lastPacketForNat[IDX_Y]) {
                    System.out.printf("Y delivered twice in a row: %d%n", lastPacketValue);
                    break;
                } else {
                    lastPacketValue = lastPacketForNat[IDX_Y];
                }
                packetsQueue.get(0L).add(natPacket);
            }
        }
    }

    private boolean isNetworkIdle() {
        return !packetsQueue.values()
                .stream()
                .map(queue -> !queue.isEmpty())
                .reduce((areNotEmpty, isNotEmpty) -> areNotEmpty || isNotEmpty)
                .orElseThrow(() -> new RuntimeException("error on checking if network is idle"));
    }

    private void runNetwork(List<String> lines) {
        long[] programBackup = Aoc2019Utils.loadProgram(lines);

        programBackup = Aoc2019Utils.copyToLargerMemory(programBackup, 6000);

        for (int address = 0; address <= MAX_AVAILABLE_ADDRESS; address++) {
            long[] programCopy = Aoc2019Utils.copyToLargerMemory(programBackup, 6000);
            computerStates.put(address, new NicState(address, programCopy));
            runNetworkInterfaceController(computerStates.get(address));
        }
//        System.out.println("-----------------------------");

        while (!isValueFor255Set[0]) {
            for (int i = 0; i <= MAX_AVAILABLE_ADDRESS; i++) {
                runNetworkInterfaceController(computerStates.get(i));
            }
//            System.out.println("-----------------------------");
        }
    }

    private HashMap<Long, Queue<long[]>> initializePacketsQueue() {
        HashMap<Long, Queue<long[]>> queueMap = new HashMap<>();
        for (long i = 0; i <= MAX_AVAILABLE_ADDRESS; i++) {
            queueMap.put(i, new LinkedBlockingQueue<>());
        }
        return queueMap;
    }

    private void runNetworkInterfaceController(NicState state) {
        boolean[] shouldPause = {false};
        Day05 intComp = new Day05();
        intComp.setInstructionPointerListener(instructionPointer -> {
            state.lastInstructionPointer = instructionPointer;
            return shouldPause[0];
        });

        intComp.setIoListeners(() -> {
//            System.out.println("input");
            if (state.inputCounter == -1) {
                state.inputCounter = 0;
                if (state.isBootingUp) {
                    state.isBootingUp = false;
                    shouldPause[0] = true;
                    return state.address;
                }
            }
            if (state.inputCounter == 0) {
                if (packetsQueue.get(state.address).isEmpty()) {
//                    System.out.println("empty queue");
                    shouldPause[0] = true;
                    return -1;
                } else {
//                    System.out.println("not empty queue");
                    state.receivedPacket = packetsQueue.get(state.address).remove();
//                    System.out.printf("received packet: %s%n", state);
                    state.inputCounter++;
                    return state.receivedPacket[IDX_X];
                }
            } else if (state.inputCounter == 1) {
                state.inputCounter = 0;
                shouldPause[0] = true;
                return state.receivedPacket[IDX_Y];
            }

            throw new RuntimeException();
        }, (output, iPointer) -> {
//            System.out.println("output");
            if (state.outputIdx == PACKET_LENGTH - 1) {
                state.sentPacket[state.outputIdx] = output;
                long[] newPacket = new long[PACKET_LENGTH];
                System.arraycopy(state.sentPacket, 0, newPacket, 0, PACKET_LENGTH);

                if (isSentTo255(newPacket)) {
                    isValueFor255Set[0] = true;
                    lastPacketForNat = newPacket;
                    System.out.printf("Packet sent to 255, Y=%d%n", lastPacketForNat[IDX_Y]);
                    return false;
                }

                shouldPause[0] = true;
//                System.out.printf("sending %s%n", Arrays.toString(newPacket));
                packetsQueue.get(newPacket[IDX_ADDRESS]).add(newPacket);

                state.outputIdx = 0;
                return false;
            }

            shouldPause[0] = false;
            state.sentPacket[state.outputIdx] = output;
            state.outputIdx++;
            return false;
        }, () -> System.out.println("End of program"));

//        System.out.printf("\n> running program %s%n", state);
        intComp.runProgram(state.program, new long[2], state.lastInstructionPointer);
    }

    private boolean isSentTo255(long[] newPacket) {
        return newPacket[IDX_ADDRESS] == NAT_ADDRESS;
    }

    /**
     * The Network Interface Controller (NIC) state
     */
    static class NicState {
        long address;
        boolean isBootingUp = true;
        int outputIdx = 0;
        long[] receivedPacket = new long[PACKET_LENGTH];

        int inputCounter = -1;
        long[] sentPacket = new long[PACKET_LENGTH];

        int lastInstructionPointer = 0;

        long[] program;

        public NicState(int address, long[] program) {
            this.address = address;
            this.program = program;
        }

        @Override
        public String toString() {
            return "PC{" +
                    String.format("ADDR=%2d", address) +
                    String.format(", BOOTING=%s", isBootingUp ? "T" : "F") +
                    ", OUT_IDX=" + outputIdx +
                    ", R_PCKT=" + Arrays.toString(receivedPacket) +
                    ", INPUT_COUNT=" + inputCounter +
                    ", S_PCKT=" + Arrays.toString(sentPacket) +
                    ", LAST_IPNTR=" + lastInstructionPointer +
                    //", PROG=" + program +
                    '}';
        }
    }
}
