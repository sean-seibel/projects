package view;

import controller.ImageProcessorControllerFeatures;
import model.Image;

/**
 * Represents Graphical User Interface for an Image Processor.
 */
public interface ImageProcessorGUIView {

  /**
   * Sets the image that is currently displayed.
   * @param im image to display.
   */
  void setImage(Image im);

  /**
   * Passes the controller to the GUI View.
   * @param cont controller to pass.
   */
  void passFeatures(ImageProcessorControllerFeatures cont);

  /**
   * Displays given message.
   * @param message string.
   */
  void showMessage(String message);
}
