import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 implements IAocTask {

    private static final int SIDE_SIZE = 1000;

    private static final String CLAIM_REGEX = "#([0-9]+) @ ([0-9]+),([0-9]+): ([0-9]+)x([0-9]+)";

    private HashMap<Integer, Integer> fabricClaimCounts;
    private HashMap<Integer, HashSet<Integer>> fabricClaimsPerSquareInch;
    private HashSet<Integer> fabricClaimsOverlappingWithAnyOther = new HashSet<>();
    private HashSet<Integer> allFabricClaims = new HashSet<>();

    @Override
    public String getFileName() {
        return "input_03.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        fabricClaimCounts = initializeFabricClaims();

        Pattern pattern = Pattern.compile(CLAIM_REGEX);
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) throw new RuntimeException(String.format("No claims found in line %s", line));
            FabricClaim fabricClaim = createFabricClaim(matcher);
            updateFabricClaims(fabricClaim, (claim, squareInchId) -> fabricClaimCounts.put(squareInchId, fabricClaimCounts.get(squareInchId) + 1));
        }

        System.out.println(fabricClaimCounts.values().stream().filter(claimCount -> claimCount > 1).count());
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        fabricClaimsPerSquareInch = initializeFabricClaimsPerSquareInch();

        Pattern pattern = Pattern.compile(CLAIM_REGEX);

        IClaimUpdate claimUpdate = (claim, squareInchId) -> {
            fabricClaimsPerSquareInch.get(squareInchId).add(claim.getClaimId());

            if (fabricClaimsPerSquareInch.get(squareInchId).size() > 1) {
                fabricClaimsOverlappingWithAnyOther.addAll(fabricClaimsPerSquareInch.get(squareInchId));
            }
            allFabricClaims.add(claim.getClaimId());
        };

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) throw new RuntimeException(String.format("No claims found in line %s", line));
            FabricClaim fabricClaim = createFabricClaim(matcher);
            updateFabricClaims(fabricClaim, claimUpdate);
        }

        for (Integer claimId : allFabricClaims) {
            if (!fabricClaimsOverlappingWithAnyOther.contains(claimId)) {
                System.out.println(claimId);
            }
        }
    }

    private FabricClaim createFabricClaim(Matcher matcher) {
        return new FabricClaim(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(4)),
                Integer.parseInt(matcher.group(5)));
    }

    private void updateFabricClaims(FabricClaim fabricClaim, IClaimUpdate iClaimUpdate) {
        int leftOffset = fabricClaim.getLeftOffset();
        int topOffset = fabricClaim.getTopOffset();
        int width = fabricClaim.getWidth();
        int height = fabricClaim.getHeight();

        for (int column = leftOffset; column < leftOffset + width; column++) {
            for (int row = topOffset; row < topOffset + height; row++) {
                int squareInchId = row * SIDE_SIZE + column % SIDE_SIZE;
                try {
                   iClaimUpdate.execute(fabricClaim, squareInchId);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private HashMap<Integer, Integer> initializeFabricClaims() {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < SIDE_SIZE * SIDE_SIZE; i++) {
            map.put(i, 0);
        }

        return map;
    }

    private HashMap<Integer, HashSet<Integer>> initializeFabricClaimsPerSquareInch() {
        HashMap<Integer, HashSet<Integer>> map = new HashMap<>();

        for (int i = 0; i < SIDE_SIZE * SIDE_SIZE; i++) {
            map.put(i, new HashSet<>());
        }
        return map;
    }

    private class FabricClaim {
        private int leftOffset;
        private int topOffset;
        private int width;
        private int height;
        private int claimId;

        private FabricClaim(int claimId, int leftOffset, int topOffset, int width, int height) {
            this.claimId = claimId;
            this.leftOffset = leftOffset;
            this.topOffset = topOffset;
            this.width = width;
            this.height = height;
        }


        int getLeftOffset() {
            return leftOffset;
        }

        int getTopOffset() {
            return topOffset;
        }

        int getWidth() {
            return width;
        }

        int getHeight() {
            return height;
        }

        int getClaimId() {
            return claimId;
        }
    }

    interface IClaimUpdate {
        void execute(FabricClaim claim, int squareInchId);
    }
}
