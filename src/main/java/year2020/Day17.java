package year2020;

import aoc.IAocTask;

import java.util.List;

public class Day17 implements IAocTask {
    private static final char ACTIVE = '#';
    private static final char INACTIVE = '.';
    private static final int[] dx = {-1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1};
    private static final int[] dy = {-1, -1, -1, 0, 0, 0, 1, 1, 1, -1, -1, -1, 0, 0, 0, 1, 1, 1, -1, -1, -1, 0, 0, 0, 1, 1, 1};
    private static final int[] dz = {-1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private static final int NEIGHS = 27;
    private static final int DIM_SIZE = 50;
    private char[][][][] cube;
    private char[][][][] backup;

    @Override
    public String getFileName() {
        return "aoc2020/input_17.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        backup = new char[DIM_SIZE][DIM_SIZE][DIM_SIZE][DIM_SIZE];
        cube = initCube();
        loadInput(cube, lines);
        int active = countActiveAtStage(6);
        System.out.println(active);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        return;
    }

    private void loadInput(char[][][][] cube, List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(0).length(); j++) {
                cube[DIM_SIZE / 2 + i][DIM_SIZE / 2 + j][DIM_SIZE / 2][DIM_SIZE / 2] = lines.get(i).charAt(j);
            }
        }
    }

    private int countActiveAtStage(int stages) {
        for (int stage = 0; stage < stages; stage++) {
            copyTo(cube, backup);
            update(cube, backup);
        }
        return countActiveAtStage(cube);
    }

    private void update(char[][][][] cube, char[][][][] backup) {
        for (int i = 0; i < DIM_SIZE; i++) {
            for (int j = 0; j < DIM_SIZE; j++) {
                for (int k = 0; k < DIM_SIZE; k++) {
                    for (int l = 0; l < DIM_SIZE; l++) {
                        int active = countActiveNeighbours(i, j, k, l, backup);
                        if (backup[i][j][k][l] == ACTIVE && (active == 2 || active == 3)) {
                            cube[i][j][k][l] = ACTIVE;
                        } else if (backup[i][j][k][l] == INACTIVE && active == 3) {
                            cube[i][j][k][l] = ACTIVE;
                        } else {
                            cube[i][j][k][l] = INACTIVE;
                        }
                    }
                }
            }
        }
    }

    private int countActiveNeighbours(int x, int y, int z, int w, char[][][][] backup) {
        int count = 0;
        for (int neighIdx = 0; neighIdx < NEIGHS; neighIdx++) {
            int nX = x + dx[neighIdx];
            int nY = y + dy[neighIdx];
            int nZ = z + dz[neighIdx];

            int[] dw;
            if (nX == x && nY == y && nZ == z) {
                dw = new int[]{-1, 1};
            } else {
                dw = new int[]{-1, 0, 1};
            }

            for (int i = 0; i < dw.length; i++) {
                int nW = w + dw[i];
                if (isActive(nX, nY, nZ, nW, backup)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isActive(int nX, int nY, int nZ, int nW, char[][][][] backup) {
        if (!isInRange(nX) || !isInRange(nY) || !isInRange(nZ) || !isInRange(nW)) {
            return false;
        }
        return backup[nX][nY][nZ][nW] == ACTIVE;
    }

    private boolean isInRange(int coord) {
        return coord >= 0 && coord < DIM_SIZE;
    }

    private int countActiveAtStage(char[][][][] cube) {
        int count = 0;
        for (int i = 0; i < DIM_SIZE; i++) {
            for (int j = 0; j < DIM_SIZE; j++) {
                for (int k = 0; k < DIM_SIZE; k++) {
                    for (int l = 0; l < DIM_SIZE; l++) {
                        if (cube[i][j][k][l] == ACTIVE) count++;
                    }
                }
            }
        }
        return count;
    }

    private void copyTo(char[][][][] cube, char[][][][] backup) {
        for (int i = 0; i < DIM_SIZE; i++) {
            for (int j = 0; j < DIM_SIZE; j++) {
                for (int k = 0; k < DIM_SIZE; k++) {
                    for (int l = 0; l < DIM_SIZE; l++) {
                        backup[i][j][k][l] = cube[i][j][k][l];
                    }
                }
            }
        }
    }

    private char[][][][] initCube() {
        var cube = new char[DIM_SIZE][DIM_SIZE][DIM_SIZE][DIM_SIZE];
        for (int i = 0; i < DIM_SIZE; i++) {
            for (int j = 0; j < DIM_SIZE; j++) {
                for (int k = 0; k < DIM_SIZE; k++) {
                    for (int l = 0; l < DIM_SIZE; l++) {
                        cube[i][j][k][l] = INACTIVE;
                    }
                }
            }
        }
        return cube;
    }
}
