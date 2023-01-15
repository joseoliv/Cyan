package saci;

@FunctionalInterface
public interface  Function2<R, S> {
    void eval(R r, S s);
}