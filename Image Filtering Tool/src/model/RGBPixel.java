package model;

/**
 * Represents a pixel with Red, Green, Blue values, which can report information about its color,
 *   and compute versions of itself which are grayscaled or of different brightness.
 */
public class RGBPixel implements Pixel {

  /**
   * Invariant: r, g, and b will always be in the range [0,255].
   */
  private final int r;
  private final int g;
  private final int b;

  /**
   * Creates RGB pixel with given RGB values.
   *
   * @param r red value.
   * @param g green value.
   * @param b blue value.
   * @throws IllegalArgumentException if R,G,B value is outside of [0,255].
   */
  public RGBPixel(int r, int g, int b) throws IllegalArgumentException {
    if (this.outsideRGBRange(r) || this.outsideRGBRange(g) || this.outsideRGBRange(b)) {
      throw new IllegalArgumentException("R,G,B values must all be within [0,255]");
    }
    this.r = r;
    this.g = g;
    this.b = b;
  }

  @Override
  public int getComponent(ColorComponent comp) {
    return this.getComponentHelp(comp);
  }

  private int getComponentHelp(ColorComponent comp) {
    switch (comp) {
      case Red:
        return this.r;
      case Green:
        return this.g;
      case Blue:
        return this.b;
      case Luma:
        return (int) Math.round(0.2126 * this.r + 0.7152 * g + 0.0722 * b);
      case Value:
        return Math.max(r, Math.max(g, b));
      case Intensity:
        return (this.r + this.g + this.b) / 3;
      default:
        return 0;
    }
  }

  @Override
  public Pixel grayscale(ColorComponent comp) {
    return new RGBPixel(this.getComponentHelp(comp),
            this.getComponentHelp(comp),
            this.getComponentHelp(comp));
  }

  /**
   * returns new RGBPixel with brightness adjusted by delta. If RGB value goes outside of
   * range [0,255] after brightness is adjusted, the value will be defaulted to either 0 or
   * 255 depending on whether the adjusted value is greater than 255 or less than 0.
   *
   * @param delta amount to change brightness by, can be positive or negative integer.
   * @return new RGBPixel with changed brightness.
   */
  @Override
  public Pixel adjustBrightness(int delta) {
    return new RGBPixel(this.withinRGBOrDefault(this.r + delta),
            this.withinRGBOrDefault(this.g + delta),
            this.withinRGBOrDefault(this.b + delta));
  }

  @Override
  public Pixel transformColor(double[][] matrix) throws IllegalArgumentException {
    if (matrix.length != 3) {
      throw new IllegalArgumentException("matrix not 3x3");
    }

    if (matrix[0].length != 3 || matrix[1].length != 3 || matrix[2].length != 3) {
      throw new IllegalArgumentException("matrix not 3x3");
    }

    int newRed = (int) Math.round(r * matrix[0][0] + g * matrix[0][1] + b * matrix[0][2]);
    int newGreen = (int) Math.round(r * matrix[1][0] + g * matrix[1][1] + b * matrix[1][2]);
    int newBlue = (int) Math.round(r * matrix[2][0] + g * matrix[2][1] + b * matrix[2][2]);

    newRed = withinRGBOrDefault(newRed);
    newGreen = withinRGBOrDefault(newGreen);
    newBlue = withinRGBOrDefault(newBlue);

    return new RGBPixel(newRed, newGreen, newBlue);
  }

  //checks if given value is within 0 <= x <= 255, if higher than
  //255 or less than 0 returns 255, or 0 respectively.
  private int withinRGBOrDefault(int value) {
    if (value < 0) {
      return 0;
    } else if (value > 255) {
      return 255;
    } else {
      return value;
    }
  }

  //true if value is greater than 255 or less than 0
  private boolean outsideRGBRange(int value) {
    return value > 255 || value < 0;
  }

}
