package year2019.utils;

import java.util.Objects;

public class Pair<T> {
    public T x;
    public T y;

    public Pair(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public Pair(Pair<T> pair) {
        this(pair.x, pair.y);
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair<?> pair = (Pair<?>) o;

        if (!Objects.equals(x, pair.x)) return false;
        return Objects.equals(y, pair.y);
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }
}
