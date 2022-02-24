package model.util;

import java.util.Objects;

//immutable point
public class Point {

  private final int x;
  private final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Point(int[] c) {
    x = c[0];
    y = c[1];
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int[] getCoords() {
    return new int[]{x, y};
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Point point = (Point) o;
    return x == point.x && y == point.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
