package view;

import java.io.IOException;

/**
 * Represents a view for an image processor which can send text messages to some destination.
 */
public class ImageProcessorTextView implements ImageProcessorView {

  private final Appendable out;

  /**
   * Constructs the text view with some Appendable as its destination.
   * @param out the destination for rendered messages.
   */
  public ImageProcessorTextView(Appendable out) {
    this.out = out;
  }

  @Override
  public void renderMessage(String message) throws IOException {
    out.append(message);
  }
}
