package me.hammerle.mp.snuviscript;

public class Wrapper<T> {
    private T t;

    public Wrapper(T t) {
        this.t = t;
    }

    public void set(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }
}
