package model;

/**
 * Represents a system of storing and modifying images.
 */
public interface ImageProcessorModel {

  /**
   * Adds an image into the processor, which will be referred to by a certain name.
   * If the name already exists, that image will be overwritten.
   *
   * @param name the name to be used within the model to refer to this image
   * @param im   the image
   */
  void addImage(String name, Image im);

  /**
   * Produces a copy of the image with a given name in the model.
   *
   * @param name name of the image
   * @return a copy of the image, as an Image object
   * @throws IllegalArgumentException if an image with this name does not exist in the model
   */
  Image returnImage(String name) throws IllegalArgumentException;

  /**
   * Grayscales image with given name in model by specified color component
   * and stores result in model with given newName.
   * If the new name already exists, that image will be overwritten.
   *
   * @param name    of image to apply grayscale on.
   * @param newName to save result to in model.
   * @param comp    ColorComponent to grayscale by.
   * @throws IllegalArgumentException if image with given name does not exist in model.
   */
  void grayscaleImage(String name, String newName, ColorComponent comp)
          throws IllegalArgumentException;

  /**
   * Flips image with given name in model horizontally, saves result in model under
   * given new name.
   * If the new name already exists, that image will be overwritten.
   *
   * @param name    of image to flip horizontally.
   * @param newName to save result to in model.
   * @throws IllegalArgumentException if image with given name does not exist in model.
   */
  void flipImageHorizontal(String name, String newName)
          throws IllegalArgumentException;

  /**
   * Flips image with given name in model horizontally, saves result in model under
   * given new name.
   * If the new name already exists, that image will be overwritten.
   *
   * @param name    of image to flip horizontally.
   * @param newName to save result to in model.
   * @throws IllegalArgumentException if image with given name does not exist in model.
   */
  void flipImageVertical(String name, String newName)
          throws IllegalArgumentException;

  /**
   * Adjusts brightness by given delta of image with given name in model,
   * saves result in model under given new name.
   * If the new name already exists, that image will be overwritten.
   *
   * @param name    of image to adjust brightness for.
   * @param newName to save the result of adjusting brightness under.
   * @param delta   amount to change image's brightness by.
   * @throws IllegalArgumentException if image with given name does not exist in model.
   */
  void adjustImageBrightness(String name, String newName, int delta)
          throws IllegalArgumentException;

  /**
   * Applies a matrix multiplication to the RGB color of an image, and stores a new image,
   *   with this new coloring.
   * @param matrix the transformation matrix
   * @throws IllegalArgumentException if the array is not 3x3
   */
  void transformImageColor(String name, String newName, double[][] matrix)
          throws IllegalArgumentException;

  /**
   * Applies a kernel-based filter to the image, and stores the result under a new name.
   * @param name name of image to filter.
   * @param newName name to save the filtered image under.
   * @param kernel the kernel with which to filter the image.
   * @throws IllegalArgumentException if the kernel is non-rectangular, or one of its dimensions is
   *                                  not odd.
   */
  void filterImage(String name, String newName, double[][] kernel)
          throws IllegalArgumentException;
}
