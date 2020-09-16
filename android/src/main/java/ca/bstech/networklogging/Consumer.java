package ca.bstech.networklogging;

public interface Consumer<T> {
    void accept(T result);
}
