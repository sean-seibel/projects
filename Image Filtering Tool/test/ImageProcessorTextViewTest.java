import org.junit.Test;

import java.io.IOException;

import view.ImageProcessorTextView;
import view.ImageProcessorView;

import static org.junit.Assert.assertEquals;

/**
 * tests ImageProcessorTextView class.
 */
public class ImageProcessorTextViewTest {


  /**
   * tests renderMessage function in ImageProcessorTextView.
   */
  @Test
  public void testRenderMessage() {
    StringBuilder s = new StringBuilder();
    ImageProcessorView view = new ImageProcessorTextView(s);

    try {
      view.renderMessage("Hello\n");
      assertEquals("Hello\n", s.toString());
      view.renderMessage("Arguments: \n");
      assertEquals("Hello\nArguments: \n", s.toString());
      view.renderMessage("12\n");
      assertEquals("Hello\nArguments: \n12\n", s.toString());
      view.renderMessage("This is a test.\n");
      assertEquals("Hello\nArguments: \n12\nThis is a test.\n", s.toString());
    } catch (IOException i) {
      s.append("unable to render test.");
    }
    assertEquals("Hello\nArguments: \n12\nThis is a test.\n", s.toString());
  }

}
