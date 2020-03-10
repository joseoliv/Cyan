package saci;

@FunctionalInterface
public interface  Function2R<T, S, R> {
    R eval(T r, S s);
}