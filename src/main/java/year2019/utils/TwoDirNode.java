package year2019.utils;

public class TwoDirNode<T> {
    T value;
    public TwoDirNode<T> left;
    public TwoDirNode<T> right;

    public TwoDirNode(T value) {
        this.value = value;
    }
}
