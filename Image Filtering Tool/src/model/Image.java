package model;

/**
 * Represents a rectangular image which can return information about itself, and return new
 * modified versions of itself.
 */
public interface Image {

  /**
   * Gets the height of this image.
   * @return the image's height in pixels.
   */
  int getHeight();

  /**
   * Gets the width of this image.
   * @return the image's width in pixels.
   */
  int getWidth();

  /**
   * Returns a copy of the pixel at the given location.
   *
   * @param row the row of the pixel to copy.
   * @param col the column of the pixel to copy.
   * @return a new Pixel with the same color as the pixel that was at that location in the image.
   */
  Pixel getPixelAt(int row, int col) throws IllegalArgumentException;

  /**
   * Reflects this image across its center row.
   * Does not modify this image, but rather returns a new image with this effect.
   * @return a new image which is the reflection of this image.
   */
  Image flipVertical();

  /**
   * Reflects this image across its center column.
   * Does not modify this image, but rather returns a new image with this effect.
   * @return a new image which is the reflection of this image.
   */
  Image flipHorizontal();

  /**
   * Adjusts the image's brightness up or down.
   * Does not modify this image, but rather returns a new image with this effect.
   * @param delta the amount by which to change the brightness. Can be positive or negative.
   * @return a new, appropriately brightened or darkened version of this image.
   */
  Image adjustBrightness(int delta);

  /**
   * Grayscales this image based on a certain component.
   * Does not modify this image, but rather returns a new image with this effect.
   * @param comp the component by which to grayscale.
   * @return a new, grayscale version of this image.
   */
  Image grayscale(ColorComponent comp);

  /**
   * Applies a matrix multiplication to the color of this image, and returns a new image, with this
   *   new coloring.
   * @param matrix the transformation matrix
   * @return the new, separate image with the transformed color.
   * @throws IllegalArgumentException if the array is not 3x3
   */
  Image transformColor(double[][] matrix) throws IllegalArgumentException;

  /**
   * Applies a filter to this image based on a given kernel, and returns a new image, with this
   *   filter applied.
   * @param kernel the transformation kernel.
   * @return the new, separate image with the filter applied.
   * @throws IllegalArgumentException if the array is not rectangular, or one of its dimensions
   *                                  is not odd.
   */
  Image filter(double[][] kernel) throws IllegalArgumentException;

  /**
   * Applies a mosiacking filter to the image to produce a new image.
   * @param seedCount number of mosaic "tiles" of different color in the produced image.
   * @return the mosaicked image.
   * @throws IllegalArgumentException if seed count is < 1
   */
  Image mosaic(int seedCount) throws IllegalArgumentException;
}
