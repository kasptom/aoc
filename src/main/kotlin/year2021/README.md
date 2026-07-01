# Advent of Code 2021 — Day 22: Reactor Reboot

## The problem

Each line is a `on`/`off` instruction over an axis-aligned cuboid of unit cubes, e.g.

```
on x=10..12,y=10..12,z=10..12
off x=9..11,y=9..11,z=9..11
```

Instructions are applied in order; later instructions overwrite earlier ones in the
overlapping region. We must count how many cubes are lit at the end.

- **Part one** considers only instructions fully inside the `-50..50` init region.
- **Part two** considers every instruction — coordinates reach into the millions, so a
  literal grid (~10^21 cubes) is impossible.

## What was wrong with the original implementation

The original solution tried to keep a list of **non-overlapping "ON" cuboids** and, for
every new instruction, *split space into sub-cuboids* so the new piece could be merged
without overlap (`collide` → `createSmallerCuboids`).

Two problems:

1. **Correctness — fragile boundary math.** `createSmallerCuboids` took the 4 sorted
   coordinates per axis (2 from each cuboid), formed 3 slabs, and adjusted slab edges
   with hand-written `+1` / `-1` offsets:

   ```
   slab0 = [a, b-1]   slab1 = [b, c]   slab2 = [c+1, d]
   ```

   This is only valid when the four coordinates are *strictly* increasing
   (`a < b < c < d`). The moment two cuboids **share a face or a coordinate**
   (extremely common in the real input), the slabs collapse or shift and stop aligning
   with the original cuboid edges. The result was sub-cuboids that overlapped each other
   — precisely the `overlapping cuboids detected` corner case the original code had to
   guard against with runtime assertions and a `history` log. The split approach can be
   made correct, but it is very easy to get subtly wrong, and this one was.

2. **Efficiency.** Every instruction materialized up to `3 × 3 × 3 = 27` sub-cuboids per
   pairwise collision and kept a growing list of disjoint ON pieces, re-splitting them on
   every step. That is a lot of allocation and bookkeeping (plus the defensive
   `findOverlappingCuboids` O(n²) scan *inside* the loop) for what should be a cheap
   computation.

## The fix — inclusion–exclusion with signed cuboids

Instead of keeping a partition of space, we keep a list of **signed cuboids** whose
signed volumes always sum to the current lit volume. No splitting, no shared-boundary
edge cases.

For each incoming instruction cuboid `C`:

1. For every already-placed cuboid `E` (with sign `s`) that **intersects** `C`, add their
   intersection with the **opposite sign** `-s`. This cancels the volume that was
   previously counted in `E` over the region `C` now overrides — whether `C` is `on` or
   `off`.
2. If `C` is an `on` instruction, additionally add `C` itself with sign `+1`.

The answer is simply `Σ sign · volume`.

```kotlin
fun countOn(operations: List<Operation>): Long {
    val placed = mutableListOf<SignedCuboid>()
    for (operation in operations) {
        val cuboid = operation.toCuboid()
        val additions = mutableListOf<SignedCuboid>()
        for ((existing, sign) in placed) {
            val intersection = cuboid.intersect(existing) ?: continue
            additions += SignedCuboid(intersection, -sign)
        }
        if (operation.state == Operation.State.ON) {
            additions += SignedCuboid(cuboid, 1)
        }
        placed += additions
    }
    return placed.sumOf { (cuboid, sign) -> sign * cuboid.volume() }
}
```

The only geometric primitive needed is `intersect`, which is unambiguous and has no
off-by-one traps:

```kotlin
fun intersect(other: Cuboid): Cuboid? {
    val minX = maxOf(min.x, other.min.x); val maxX = minOf(max.x, other.max.x)
    val minY = maxOf(min.y, other.min.y); val maxY = minOf(max.y, other.max.y)
    val minZ = maxOf(min.z, other.min.z); val maxZ = minOf(max.z, other.max.z)
    if (minX > maxX || minY > maxY || minZ > maxZ) return null
    return Cuboid(Point3d(minX, minY, minZ), Point3d(maxX, maxY, maxZ))
}
```

### Why it is correct

It is the inclusion–exclusion principle applied incrementally. At every step the signed
list is an exact algebraic representation of the lit set: re-lighting an already-lit
region adds `+C` and `-overlap`, which nets to lighting only the new area; turning a
region off adds only `-overlap` terms, removing exactly the lit volume there. Because we
only ever add *intersections of existing cuboids*, the volumes are always integers and the
sum is exact (use `Long` — part two exceeds `Int`).

### Why it is efficient

`O(n²)` intersection tests over `n` instructions, each test being a handful of
`min`/`max` comparisons. No space partitioning, no defensive overlap scanning, far fewer
allocations. Comfortably solves part two in well under a second.

## Notable code changes

- `Cuboid` lost its `type` field and the entire `collide` / `CollisionResult` /
  `createSmallerCuboids` machinery; it now only carries `min`/`max` plus `intersect` and
  `volume`.
- `solvePartOne` reuses `countOn` on the init-range subset; the old per-cube
  `MutableMap<Point3d, State>` grid is gone.
- `volume` is inclusive on both corners: `(maxX-minX+1)*(maxY-minY+1)*(maxZ-minZ+1)`.

## Tests

`Day22Test` was rewritten to target the new design and, crucially, to validate
correctness independently of the algorithm:

- `intersect` / `volume` unit tests (overlap, shared-corner volume 1, disjoint → null).
- The canonical 4-line example with a re-light → **39**.
- The larger part-one example restricted to the init region → **590784**.
- A **brute-force property test**: random small cuboids are toggled on a real
  `HashSet` grid (ground truth) and compared against `countOn`. This would immediately
  catch any shared-boundary / off-by-one regression — the exact class of bug that broke
  the original splitting approach.

## Running

The Kotlin 1.8.0 plugin cannot target JVM 21, so build/test with JDK 17:

```bash
./gradlew test --tests "year2021.Day22Test" \
  -Dorg.gradle.java.home="$HOME/.sdkman/candidates/java/17.0.14-tem"
```

`getFileName()` points at `aoc2021/input_22.txt` (your puzzle input, git-ignored under
`src/main/resources/`).
