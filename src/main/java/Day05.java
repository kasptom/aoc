import java.util.Arrays;
import java.util.List;

public class Day05 implements IAocTask {

    @Override
    public String getFileName() {
        return "input_05.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        char[] chars = lines.get(0).toCharArray();
        System.out.printf("before: %d\n", chars.length);

        PolymerUnit first = createPolymer(chars);
        first = compressPolymer(first);
        printPolymer(first);

        System.out.printf("after compression: %d\n", getLength(first));
    }

    private PolymerUnit createPolymer(char[] chars) {
        PolymerUnit first = new PolymerUnit(chars[0]);
        PolymerUnit pointer = first;

        for (int i = 1; i < chars.length; i++) {
            pointer.next = new PolymerUnit(chars[i]);
            pointer = pointer.next;
        }
        printPolymer(first);
        return first;
    }

    private PolymerUnit compressPolymer(PolymerUnit first) {
        PolymerUnit prev;
        PolymerUnit pointer;
        boolean isReduced = true;

        while (isReduced) {
            isReduced = false;

            while(first.reactsWithNext()) {
                first = first.next.next;
                isReduced = true;
            }

            prev = null;
            pointer = first;

            while (pointer != null) {
                if (pointer.reactsWithNext()) {
                    isReduced = true;
                    pointer = pointer.next.next;
                    if (prev != null) {
                        prev.next = pointer;
                    }
                } else {
                    prev = pointer;
                    pointer = pointer.next;
                }
            }
//            int length = getLength(first);
//            System.out.printf("after compression: %d\n", length);
//            printPolymer(first);
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

        String line = lines.get(0);
        int bestShort = line.length();

        for (char letter = 'a'; letter <= 'z'; letter++) {
            char capLetter = (char)(letter - 32);

            String smallBig = Arrays.toString(new char[]{letter, capLetter});
            String bigSmall = Arrays.toString(new char[]{capLetter, letter});

            System.out.println(smallBig);
            System.out.println(bigSmall);

            line = lines.get(0).replaceAll(smallBig, "").replaceAll(bigSmall, "");

            PolymerUnit first = createPolymer(line.toCharArray());
            first = compressPolymer(first);
            int currentLength = getLength(first);

            if (currentLength < bestShort) {
                bestShort = currentLength;
            }
        }

        System.out.println(bestShort);
    }

    class PolymerUnit {
        PolymerUnit next;
        char value;

        PolymerUnit(char value) {
            this.value = value;
        }

        boolean reactsWithNext() {
            return next != null && reactsWith(next);
        }

        private boolean reactsWith(PolymerUnit other) {
            return Math.abs((int) other.value - (int) this.value) == 32;
        }
    }
}
