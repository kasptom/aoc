import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Day25 implements IAocTask {

    private int componentsCount = 0;
    private HashMap<Integer, FourDimPoint> idToParent = new HashMap<>();
    private HashMap<Integer, FourDimPoint> idToPoint = new HashMap<>();

    @Override
    public String getFileName() {
//        return "input_25_simple_3.txt";
        return "input_25_simple_5.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<FourDimPoint> points = loadPoints(lines);

        boolean isShrinking = true;
        while (isShrinking) {
            isShrinking = false;
            for(Integer pointId : idToPoint.keySet()) {
                for(Integer otherId: idToPoint.keySet()) {
                    if (pointId.equals(otherId)) continue;

                    FourDimPoint point = idToPoint.get(pointId);
                    FourDimPoint other = idToPoint.get(otherId);

                    if (find(point, other)) continue;

                    List<FourDimPoint> otherPointSiblings = points
                            .stream()
                            .filter(p -> p.getParent() == other.getParent())
                            .collect(Collectors.toList());

                    for (FourDimPoint sibling : otherPointSiblings) {
                        if (point.distance(sibling) <= 3) {
                            union(point, sibling);
                            isShrinking = true;
                        }
                    }
                }
            }
        }

        System.out.println(componentsCount);
    }

    private void union(FourDimPoint point, FourDimPoint other) {
        if (point.id == other.id) throw new RuntimeException("Trying to merge point with itself");

        idToParent.put(other.getParent().id, idToParent.get(point.id));
        componentsCount--;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    private List<FourDimPoint> loadPoints(List<String> lines) {
        ArrayList<FourDimPoint> points = new ArrayList<>();

        int id = 1;
        for (String line : lines) {
            if (!line.contains(",")) {
                System.out.println(line);
                continue;
            }

            List<Integer> coords = Arrays.stream(line.trim()
                    .split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            points.add(new FourDimPoint(id++, coords.get(0), coords.get(1), coords.get(2), coords.get(3)));
            componentsCount++;
        }
        return points;
    }

    static boolean find(FourDimPoint a, FourDimPoint b) {
        return a.getParent() == b.getParent();
    }

    class FourDimPoint {
        int id;
        int x, y, z, v;

        FourDimPoint(int id, int x, int y, int z, int v) {
            idToParent.put(id, this);
            idToPoint.put(id, this);
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
            this.v = v;
        }

        FourDimPoint getParent() {
           FourDimPoint parent = idToParent.get(id);
           if (parent.id != id) {
               idToParent.put(id, parent.getParent());
           }

           return idToParent.get(id);
        }

        int distance(FourDimPoint other) {
            return dist(x, other.x) + dist(y, other.y) + dist(z, other.z) + dist(v, other.v);
        }

        private int dist(int a, int b) {
            return Math.abs(a - b);
        }
    }
}
