import java.util.*;
import java.util.stream.Collectors;

public class Day06 implements IAocTask {
    private Integer xMax;
    private Integer yMax;

    @Override
    public String getFileName() {
        return "input_06.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Location> locations = initializeLocations(lines);
        Location[][] area = createArea(locations);

        locations.forEach(location -> area[location.y - 1][location.x - 1] = location);

        // mozna by tez union find
        for (Location[] row : area) {
            Arrays.stream(row).forEach(location -> {
                findClosestSource(location, locations);
            });
        }
        // printArea(area);

        int largestFiniteArea = getLargestFiniteArea(area, locations);
        System.out.println(largestFiniteArea);
    }

    private int getLargestFiniteArea(Location[][] area, List<Location> locations) {
        HashSet<Integer> infiniteAreas = new HashSet<>();

        for (int i = 0; i < xMax; i++) {
            infiniteAreas.add(Math.abs(area[0][i].areaId));
            infiniteAreas.add(Math.abs(area[yMax - 1][i].areaId));
        }

        for (int i = 0; i < yMax; i++) {
            infiniteAreas.add(Math.abs(area[i][0].areaId));
            infiniteAreas.add(Math.abs(area[i][xMax - 1].areaId));
        }

        HashMap<Integer, Integer> areaSizes = new HashMap<>();

        for (Location[] row: area) {
            for (Location location : row) {
                int areaId = Math.abs(location.areaId);
                if (areaSizes.containsKey(areaId)) {
                    areaSizes.put(areaId, areaSizes.get(areaId) + 1);
                } else {
                    areaSizes.put(areaId, 1);
                }
            }
        }

        int maxArea = -1;
        for (Integer areaId : areaSizes.keySet()) {
            if (infiniteAreas.contains(areaId)) {
                continue;
            }
            if (maxArea < areaSizes.get(areaId)) {
                maxArea = areaSizes.get(areaId);
            }
        }

        return maxArea;
    }

    private void findClosestSource(Location location, List<Location> locations) {
        if (location.areaId < 0) {
            return;
        }

        int closestDist = locations.stream()
                .min(Comparator.comparingInt(other -> other.getDistance(location)))
                .map(loc -> loc.getDistance(location))
                .orElse(-1);

        List<Location> closest = locations
                .stream()
                .filter(loc -> loc.getDistance(location) == closestDist)
                .collect(Collectors.toList());

        if (closest.size() > 1) {
            location.areaId = 0;
        } else {
            location.areaId = -closest.get(0).areaId;
        }
    }

    private void printArea(Location[][] area) {
        for (int i = 0; i < yMax; i++) {
            for (int j = 0; j < xMax; j++) {
                if (area[i][j].areaId != 0) {
                    System.out.printf("%3d", area[i][j].areaId);
                } else {
                    System.out.print(" # ");
                }
            }
            System.out.println();
        }
    }

    private Location[][] createArea(List<Location> locations) {
        xMax = locations.stream()
                .max(Comparator.comparingInt(other -> other.x))
                .map(location -> location.x).orElse(-1);

        yMax = locations.stream()
                .max(Comparator.comparingInt(other -> other.y))
                .map(location -> location.y).orElse(-1);

        Location[][] area = new Location[yMax][xMax];

        for (int i=0; i<yMax; i++) {
            for (int j=0; j<xMax; j++) {
                area[i][j] = new Location(j + 1, i + 1);
            }
        }

        return area;
    }

    private List<Location> initializeLocations(List<String> lines) {
        List<Location> locations = lines.stream()
                .map(line -> {
                    String[] coords = line.split(",");
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());
                    return new Location(x, y);
                })
                .collect(Collectors.toList());

        final int[] id = {-1};
        locations.forEach(location -> {
            location.areaId = id[0];
            id[0] = id[0] -1;
        });
        return locations;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    class Location {
        final int x;
        final int y;
        int areaId = 0;

        Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getDistance(Location other) {
            return Math.abs(other.x - x) + Math.abs(other.y - y);
        }
    }
}
