import java.util.List;

public class Day05 implements IAocTask {

    private PolymerUnit first;

    @Override
    public String getFileName() {
        return "input_05_small.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        char[] chars = lines.get(0).toCharArray();

        System.out.printf("before: %d\n", chars.length);

        first = new PolymerUnit(chars[0]);
        PolymerUnit pointer = first;

        for (int i = 1; i < chars.length; i++) {
            pointer.next = new PolymerUnit(chars[i]);
            pointer = pointer.next;
        }
        printPolymer(first);

        first = compressPolymer(first);
        printPolymer(first);

        System.out.printf("after compression: %d\n", getLength(first));
    }

    private PolymerUnit compressPolymer(PolymerUnit first) {
        PolymerUnit prev;
        PolymerUnit pointer;
        boolean isReduced = true;

        while (isReduced) {
            isReduced = false;

            while(first.reactsWithNext()) {
                first = first.reduce(null);
                isReduced = true;
            }

            prev = null;
            pointer = first;

            while (pointer != null) {
                if (pointer.reactsWithNext()) {
                    pointer = pointer.reduce(prev);
                    isReduced = true;
                    if (prev == first) {
                        prev = null;
                        first = pointer;
                    }
                } else {
                    prev = pointer;
                    pointer = pointer.next;
                }
            }
            int length = getLength(first);
            System.out.printf("after compression: %d\n", length);
            printPolymer(first);
        }

        return first;
    }

    private void printPolymer(PolymerUnit first) {
        PolymerUnit pointer = first;
        while (pointer != null) {
            System.out.print(pointer.value);
            pointer = pointer.next;
        }
        System.out.println();
    }

    private int getLength(PolymerUnit first) {
        int counter = 0;
        PolymerUnit pointer = first;
        while (pointer != null) {
            counter++;
            pointer = pointer.next;
        }

        return counter;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    class PolymerUnit {
        PolymerUnit next;
        char value;

        public PolymerUnit(char value) {
            this.value = value;
        }

        boolean reactsWithNext() {
            return next != null && reactsWith(next);
        }

        PolymerUnit reduce(PolymerUnit prev) {
            if (prev != null) {
                prev.next = this.next.next;
            }

            return this.next.next;
        }

        private boolean reactsWith(PolymerUnit other) {
            return Math.abs((int) other.value - (int) this.value) == 32;
        }
    }
}
