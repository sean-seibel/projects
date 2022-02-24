package model.util;

public class Vector {

  private final double x;
  private final double y;

  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  //give magnitude in direction if v is normalized
  public double dotProduct(Vector v) {
    return v.getX() * x + v.getY() * y;
  }

  public double magnitude() {
    return Math.sqrt(x * x + y * y);
  }

  public Vector normalize() {
    double mag = this.magnitude();
    if (mag == 0.0) {
      mag = 1.0;
    }
    return new Vector(this.x / mag, this.y / mag);
  }

}
