package view;

public class Cycle <T> {

  private final T[] arr;
  private int index;

  public Cycle(T[] arr) {
    if (arr.length == 0) {
      throw new IllegalArgumentException("Cycle can't have 0 members");
    }
    this.arr = arr;
    this.index = 0;
  }

  public void next() {
    index++;
    if (index >= arr.length) {
      index = 0;
    }
  }

  public void reset() {
    this.index = 0;
  }

  public T get() {
    return arr[index];
  }
}
