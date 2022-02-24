import org.junit.Test;

import java.awt.event.ActionEvent;
import java.io.IOException;

import controller.ImageProcessorControllerFeatures;
import view.ImageProcessorGraphicsView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the ImageProcessorGraphicsView.
 */
public class ImageProcessorGraphicsViewTest {

  private class MockController implements ImageProcessorControllerFeatures {

    Appendable mockLog;

    MockController(Appendable mockLog) {
      this.mockLog = mockLog;
    }

    @Override
    public void runCommand(String command) {
      this.appendToLog(command);
    }

    @Override
    public void loadImage(String filepath) {
      this.appendToLog(filepath);
    }

    @Override
    public void saveImage(String filepath) {
      this.appendToLog(filepath);
    }

    private void appendToLog(String thing) {
      try {
        this.mockLog.append(thing + " ");
      } catch (IOException ioE) {
        fail("Couldn't append to mock log.");
      }
    }
  }

  /**
   * tests that the view passes the correct commands to the controller on given action.
   */
  @Test
  public void testView() {
    Appendable log = new StringBuilder();
    ImageProcessorControllerFeatures mock = new MockController(log);
    ImageProcessorGraphicsView view = new ImageProcessorGraphicsView();
    view.passFeatures(mock);

    ActionEvent action = new ActionEvent(view, 99, "Component Options");
    view.actionPerformed(action);
    assertEquals("", log.toString());

    action = new ActionEvent(view, 99, "red-component");
    view.actionPerformed(action);
    assertEquals("red-component ", log.toString());

    action = new ActionEvent(view, 99, "green-component");
    view.actionPerformed(action);
    assertEquals("red-component green-component ", log.toString());

    action = new ActionEvent(view, 99, "blue-component");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component ", log.toString());

    action = new ActionEvent(view, 99, "luma-component");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component ",
            log.toString());

    action = new ActionEvent(view, 99, "intensity-component");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component" +
            " intensity-component ", log.toString());

    action = new ActionEvent(view, 99, "value-component");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component" +
            " intensity-component value-component ", log.toString());

    action = new ActionEvent(view, 99, "vertical-flip");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component " +
            "intensity-component value-component vertical-flip ", log.toString());

    action = new ActionEvent(view, 99, "horizontal-flip");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component " +
            "intensity-component value-component vertical-flip horizontal-flip ", log.toString());

    action = new ActionEvent(view, 99, "blur");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component " +
            "intensity-component value-component vertical-flip horizontal-flip blur ",
            log.toString());

    action = new ActionEvent(view, 99, "sharpen");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component" +
            " intensity-component value-component vertical-flip horizontal-flip blur" +
            " sharpen ", log.toString());

    action = new ActionEvent(view, 99, "sepia");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component" +
            " intensity-component value-component vertical-flip horizontal-flip blur sharpen " +
            "sepia ", log.toString());

    action = new ActionEvent(view, 99, "grayscale");
    view.actionPerformed(action);
    assertEquals("red-component green-component blue-component luma-component " +
            "intensity-component value-component vertical-flip horizontal-flip blur sharpen " +
            "sepia grayscale ", log.toString());
  }
}