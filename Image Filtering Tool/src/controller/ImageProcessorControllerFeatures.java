package controller;

/**
 * Controller for a Graphical User Interface View.
 */
public interface ImageProcessorControllerFeatures {

  /**
   * Runs an Image Processor command specified by a specific string.
   * @param command string.
   */
  void runCommand(String command);

  /**
   * Loads an image from a specified filepath.
   * @param filepath of the image.
   */
  void loadImage(String filepath);

  /**
   * Saves an image to a specified filepath.
   * @param filepath to save to.
   */
  void saveImage(String filepath);

}
