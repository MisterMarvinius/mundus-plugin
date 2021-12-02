package me.hammerle.kp.snuviscript;

public class RingArray<T> {
    private int length = 0;
    private int index = 0;
    private final T[] data;

    @SuppressWarnings("unchecked")
    public RingArray(int maxLength) {
        data = (T[]) new Object[maxLength];
    }

    public void add(T t) {
        data[index] = t;
        length = Math.min(length + 1, data.length);
        index = (index + 1) % data.length;
    }

    public int getLength() {
        return length;
    }

    public T get(int index) {
        if(index < 0 || index >= length) {
            return null;
        }
        index = (this.index - length + index + data.length) % data.length;
        return data[index];
    }

    public void clear() {
        index = 0;
        length = 0;
    }
}
