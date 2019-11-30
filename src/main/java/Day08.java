import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Day08 implements IAocTask {
    private int currIdx = 0;
    private LicenseNode licenseTree;

    @Override
    public String getFileName() {
        return "aoc2018/input_08.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        Integer[] data = Arrays.stream(lines.get(0)
                .split(" "))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);

        LicenseNode licenseNode = createLicenseTree(data);

        this.licenseTree = licenseNode;

        int sum = sumMetadata(licenseNode);
        System.out.println(sum);
    }

    private int sumMetadata(LicenseNode licenseNode) {
        int sum = Arrays.stream(licenseNode.metadata).reduce((currSum, meta) -> currSum + meta).orElse(0);

        for (LicenseNode child : licenseNode.children) {
            sum += sumMetadata(child);
        }

        return sum;
    }

    private LicenseNode createLicenseTree(Integer[] data) {
        int childCount = data[currIdx++];
        int metaCount = data[currIdx++];
        LicenseNode licenseNode = new LicenseNode(childCount, metaCount);

        for (int i = 0; i < childCount; i++) {
            licenseNode.children[i] = createLicenseTree(data);
        }

        for (int i=0; i < metaCount; i++) {
            licenseNode.metadata[i] = data[currIdx++];
        }
        return licenseNode;
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        int value = computeRootValue(licenseTree);
        System.out.println(value);
    }

    private int computeRootValue(LicenseNode licenseTree) {
        AtomicInteger value = new AtomicInteger();
        if (licenseTree.childCount > 0) {
            Arrays.stream(licenseTree.metadata).forEach(meta -> {
                if (meta > licenseTree.childCount) {
                    value.addAndGet(0);
                } else {
                    value.addAndGet(computeRootValue(licenseTree.children[meta - 1]));
                }
            });
        } else {
            value.set(Arrays.stream(licenseTree.metadata).sum());
        }

        return value.get();
    }

    class LicenseNode {
        int childCount;
        int metadataCount;

        LicenseNode[] children;
        int[] metadata;

        LicenseNode(int childCount, int metadataCount) {
            this.childCount = childCount;
            this.metadataCount = metadataCount;
            this.children = new LicenseNode[this.childCount];
            this.metadata = new int[this.metadataCount];
        }
    }
}
