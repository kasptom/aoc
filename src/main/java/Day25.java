import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Day25 implements IAocTask {

    private int componentsCount = 0;
    private QuickUnionFind unionFind;
    private HashMap<Integer, FourDimPoint> idToPoint = new HashMap<>();

    @Override
    public String getFileName() {
//        return "input_25_simple_3.txt";
        return "input_25_simple_5.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<FourDimPoint> points = loadPoints(lines);
        unionFind = new QuickUnionFind(points.size());

        boolean isShrinking = true;
        while (isShrinking) {
            isShrinking = false;
            for (Integer pointId : idToPoint.keySet()) {
                for (Integer otherId : idToPoint.keySet()) {
                    if (pointId.equals(otherId)) continue;

                    FourDimPoint point = idToPoint.get(pointId);
                    FourDimPoint other = idToPoint.get(otherId);

                    if (unionFind.find(point.id, other.id)) continue;

                    List<FourDimPoint> otherPointSiblings = points
                            .stream()
                            .filter(p -> unionFind.find(p.id, otherId))
                            .collect(Collectors.toList());

                    for (FourDimPoint sibling : otherPointSiblings) {
                        if (point.distance(sibling) <= 3) {
                            unionFind.union(point.id, sibling.id);
                            isShrinking = true;
                        }
                    }
                }
            }
        }

        System.out.println(componentsCount);
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

    class FourDimPoint {
        int id;
        int x, y, z, v;

        FourDimPoint(int id, int x, int y, int z, int v) {
            idToPoint.put(id, this);
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
            this.v = v;
        }

        int distance(FourDimPoint other) {
            return dist(x, other.x) + dist(y, other.y) + dist(z, other.z) + dist(v, other.v);
        }

        private int dist(int a, int b) {
            return Math.abs(a - b);
        }
    }

    class QuickUnionFind {
        int[] id;
        int[] sz;

        QuickUnionFind(int maxId) {
            id = new int[maxId];
            sz = new int[maxId];

            for (int i = 0; i < id.length; i++) {
                id[i] = i + 1;
                sz[i] = 1;
            }
        }

        boolean find(int p, int q) {
            return root(p) == root(q);
        }

        void union(int p, int q) {
            int i = root(p);
            int j = root(q);

            if (sz[i-1] < sz[j-1]) {
                id[i-1] = j;
                sz[j-1] += sz[i-1];
            } else {
                id[j-1] = i;
                sz[i-1] += sz[j-1];
            }

            componentsCount--;
        }

        private int root(int p) {
            while (p != id[p - 1]) {
                p = id[p - 1];
            }
            return p;
        }
    }
}
