package controller;

/**
 * Controller for ImageProcessor.
 */
public interface ImageProcessorController {

  /**
   * Runs the ImageProcessor, grabbing input from user,
   * passing it to the model, and then passing the result to the view for display.
   *
   * @throws IllegalStateException if input or output fails.
   */
  void activateProcessor() throws IllegalStateException;
}
