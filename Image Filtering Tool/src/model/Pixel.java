package model;

/**
 * Represents a pixel in an image, with the ability to provide information about its color,
 * and provide new versions of itself with color-changing operations applied to it.
 */
public interface Pixel {

  /**
   * gets a specified component (R,G,B,Luma,Intensity,Value) of the pixel.
   *
   * @param comp Color component to fetch.
   * @return the value of this component for this pixel.
   */
  int getComponent(ColorComponent comp);

  /**
   * Returns a new pixel that is grayscaled to a specified color component.
   *
   * @param comp Color component to grayscale by.
   * @return new Pixel with values grayscaled to specified color component.
   */
  Pixel grayscale(ColorComponent comp);

  /**
   * Returns a new pixel, whose brightness has been adjusted compared to this pixel.
   * Positive values increase brightness, and negative values decrease brightness.
   *
   * @param delta amount to change brightness by, can be a positive or negative integer.
   * @return new Pixel with changed brightness.
   */
  Pixel adjustBrightness(int delta);

  /**
   * Applies a matrix multiplication to this pixel's color, and returns a new pixel, with this
   *   new color.
   * @param matrix the transformation matrix
   * @return the new, separate pixel with the transformed color.
   * @throws IllegalArgumentException if the array is not 3x3
   */
  Pixel transformColor(double[][] matrix) throws IllegalArgumentException;
}
