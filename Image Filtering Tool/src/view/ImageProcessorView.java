package view;

import java.io.IOException;

/**
 * Represents the view for an Image Processor.
 */
public interface ImageProcessorView {

  /**
   * Renders a message to some output destination.
   * @param message the message, as a string of characters, to be sent.
   * @throws IOException if there is an error in writing to the output.
   */
  void renderMessage(String message) throws IOException;
}
