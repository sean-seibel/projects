package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import mocks.MockImageProcessorModel;
import model.ImageProcessorModel;
import model.ImageProcessorModelImpl;
import view.ImageProcessorTextView;

import static org.junit.Assert.assertEquals;

/**
 * Tests the ImageProcessorController.
 */
public class ImageProcessorControllerImplTest {

  ImageProcessorModel mockModel;
  Appendable mockLog;
  Appendable viewLog;

  @Before
  public void setup() {
    mockLog = new StringBuilder();
    viewLog = new StringBuilder();
    mockModel = new MockImageProcessorModel(mockLog);
  }

  @Test
  public void testConstructor() {
    ImageProcessorController cont = new ImageProcessorControllerImpl(
            mockModel,
            new StringReader("brighten 10 a a-bright quit"),
            new ImageProcessorTextView(viewLog)
    );

    cont.activateProcessor();

    assertEquals("adjustImageBrightness a a-bright 10\n", mockLog.toString());
    assertEquals("Welcome to Image Processor. \"menu\" for command list.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Thank you for using Image Processor!\n", viewLog.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullModel() {
    new ImageProcessorControllerImpl(
            null, new StringReader("q"), new ImageProcessorTextView(viewLog));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new ImageProcessorControllerImpl(
            new MockImageProcessorModel(mockLog),
            null,
            new ImageProcessorTextView(viewLog));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullView() {
    new ImageProcessorControllerImpl(
            new MockImageProcessorModel(mockLog),
            new StringReader("q"),
            null);
  }

  @Test
  public void testCommands() {
    ImageProcessorController cont = new ImageProcessorControllerImpl(
            mockModel,
            new StringReader("lOAd test/controller/readFrom/im22.ppm im\n" +
                    "reD-comPonent im im2\n" +
                    "LUMA-component im2 im3\n" +
                    "hoRIZontal-flIp im3 im4\n" +
                    "Vertical-Flip im4 im4\n" +
                    "intensity-compoNENT im im5\n" +
                    "Blue-component im im6\n" +
                    "green-Component im im7\n" +
                    "BRIGhTEN -20 im7 im7\n" +
                    "grayscale im2 im8\n" +
                    "blur im8 im8\n" +
                    "sharpen im8 im8\n" +
                    "sepia im8 im8\n" +
                    "mEnu\n" +
                    "Save im7 test/controller/saveTo/im22.ppm\n" +
                    "qUIt"
            ),
            new ImageProcessorTextView(viewLog)
    );

    cont.activateProcessor();

    assertEquals("Welcome to Image Processor. \"menu\" for command list.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "  Command: q, quit\n" +
            "  Command: menu\n" +
            "  Command: load  |  Arguments: file-name image-name\n" +
            "  Command: save  |  Arguments: image-name file-name\n" +
            "  Command: red-component  |  Arguments: image-name new-image-name\n" +
            "  Command: green-component  |  Arguments: image-name new-image-name\n" +
            "  Command: blue-component  |  Arguments: image-name new-image-name\n" +
            "  Command: luma-component  |  Arguments: image-name new-image-name\n" +
            "  Command: value-component  |  Arguments: image-name new-image-name\n" +
            "  Command: intensity-component  |  Arguments: image-name new-image-name\n" +
            "  Command: horizontal-flip  |  Arguments: image-name new-image-name\n" +
            "  Command: vertical-flip  |  Arguments: image-name new-image-name\n" +
            "  Command: brighten  |  Arguments: brightness-change image-name new-image-name\n" +
            "  Command: grayscale  |  Arguments: image-name new-image-name\n" +
            "  Command: sepia  |  Arguments: image-name new-image-name\n" +
            "  Command: blur  |  Arguments: image-name new-image-name\n" +
            "  Command: sharpen  |  Arguments: image-name new-image-name\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Thank you for using Image Processor!\n", viewLog.toString());
    assertEquals("addImage im 2x2 image\n" +
            "grayscaleImage im im2 red\n" +
            "grayscaleImage im2 im3 luma\n" +
            "flipImageHorizontal im3 im4\n" +
            "flipImageVertical im4 im4\n" +
            "grayscaleImage im im5 intensity\n" +
            "grayscaleImage im im6 blue\n" +
            "grayscaleImage im im7 green\n" +
            "adjustImageBrightness im7 im7 -20\n" +
            "transformImageColor im2 im8 " +
            "0.2126 0.7152 0.0722 / 0.2126 0.7152 0.0722 / 0.2126 0.7152 0.0722 /\n" +
            "filterImage im8 im8 0.0625 0.125 0.0625 / 0.125 0.25 0.125 / 0.0625 0.125 0.0625 /\n" +
            "filterImage im8 im8 " +
            "-0.125 -0.125 -0.125 -0.125 -0.125 / " +
            "-0.125 0.25 0.25 0.25 -0.125 / " +
            "-0.125 0.25 1.0 0.25 -0.125 / " +
            "-0.125 0.25 0.25 0.25 -0.125 / " +
            "-0.125 -0.125 -0.125 -0.125 -0.125 /\n" +
            "transformImageColor im8 im8 " +
            "0.393 0.769 0.189 / 0.349 0.686 0.168 / 0.272 0.534 0.131 /\n" +
            "returnImage im7\n", mockLog.toString());
  }

  @Test
  public void testArgumentsDontRunCommands() {
    ImageProcessorController cont = new ImageProcessorControllerImpl(
            mockModel,
            new StringReader("load test/controller/readFrom/im22.ppm quit\n" +
                    "red-component quit menu\n" +
                    "horizontal-flip menu intensity-component\n" +
                    "vertical-flip intensity-component brighten\n" +
                    "Q"
            ),
            new ImageProcessorTextView(viewLog)
    );

    cont.activateProcessor();

    assertEquals("Welcome to Image Processor. \"menu\" for command list.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Thank you for using Image Processor!\n", viewLog.toString());
    assertEquals("addImage quit 2x2 image\n" +
            "grayscaleImage quit menu red\n" +
            "flipImageHorizontal menu intensity-component\n" +
            "flipImageVertical intensity-component brighten\n", mockLog.toString());
  }

  @Test
  public void testCommandsNotFound() {
    ImageProcessorController cont = new ImageProcessorControllerImpl(
            mockModel,
            new StringReader("lood test/controller/readFrom/im22.ppm a\n" +
                    "c\n" +
                    "load test/controller/readFrom/im22.ppm a\n" +
                    "command-activate!\n" +
                    "vertical-flip a 12\n" +
                    "give-money $100\n" +
                    "q"
            ),
            new ImageProcessorTextView(viewLog)
    );

    cont.activateProcessor();

    assertEquals("Welcome to Image Processor. \"menu\" for command list.\n" +
            "Command:\n" +
            "Command \"lood\" not found.\n" +
            "Command:\n" +
            "Command \"test/controller/readfrom/im22.ppm\" not found.\n" +
            "Command:\n" +
            "Command \"a\" not found.\n" +
            "Command:\n" +
            "Command \"c\" not found.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Command \"command-activate!\" not found.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Command \"give-money\" not found.\n" +
            "Command:\n" +
            "Command \"$100\" not found.\n" +
            "Command:\n" +
            "Thank you for using Image Processor!\n", viewLog.toString());
    assertEquals("addImage a 2x2 image\n" +
            "flipImageVertical a 12\n", mockLog.toString());
  }

  @Test
  public void testControllerErrorMessages() {
    ImageProcessorController cont = new ImageProcessorControllerImpl(
            new ImageProcessorModelImpl(),
            new StringReader("load test/controller/readFrom/image_of_your_mom.ppm im1\n" +
                    "load test/controller/readFrom/im22.ppm im2\n" +
                    "blue-component im1 im1-blue\n" +
                    "brighten fifty\n" +
                    "Quit"
            ),
            new ImageProcessorTextView(viewLog)
    );

    cont.activateProcessor();

    assertEquals("Welcome to Image Processor. \"menu\" for command list.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "File not found!\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "No such name in the system.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Incorrect form of parameters.\n" +
            "Command:\n" +
            "Thank you for using Image Processor!\n", viewLog.toString());
  }

  @Test
  public void checkFailureToReadInput() {

    class CantRead extends Reader {

      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("I can't read :(");
      }

      @Override
      public void close() throws IOException {
        //does nothing for this implementation
      }
    }

    ImageProcessorController cont = new ImageProcessorControllerImpl(
            new MockImageProcessorModel(mockLog),
            new CantRead(),
            new ImageProcessorTextView(viewLog)
    );

    try {
      cont.activateProcessor();
    } catch (IllegalStateException e) {
      assertEquals("Ran out of input", e.getMessage());
    }
  }

  @Test
  public void checkFailureToSendOutput() {
    class CantWrite implements Appendable {

      @Override
      public Appendable append(CharSequence csq) throws IOException {
        throw new IOException("Can't write :(");
      }

      @Override
      public Appendable append(CharSequence csq, int start, int end) throws IOException {
        throw new IOException("Can't write :(");
      }

      @Override
      public Appendable append(char c) throws IOException {
        throw new IOException("Can't write :(");
      }
    }

    ImageProcessorController cont = new ImageProcessorControllerImpl(
            new MockImageProcessorModel(mockLog),
            new StringReader("quit"),
            new ImageProcessorTextView(new CantWrite())
    );

    try {
      cont.activateProcessor();
    } catch (IllegalStateException e) {
      assertEquals("Input or output failed!", e.getMessage());
    }
  }

  @Test
  public void testNoInputsAfterQuit() {
    ImageProcessorController cont = new ImageProcessorControllerImpl(
            mockModel,
            new StringReader("load test/controller/readFrom/im22.ppm a\n" +
                    "brighten 10 a a-bright\n" +
                    "quit\n" +
                    "vertical-flip a-bright b\n" +
                    "save a test/controller/saveTo/a.ppm"),
            new ImageProcessorTextView(viewLog)
    );

    cont.activateProcessor();

    assertEquals("addImage a 2x2 image\n" +
            "adjustImageBrightness a a-bright 10\n", mockLog.toString());
    assertEquals("Welcome to Image Processor. \"menu\" for command list.\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Arguments:\n" +
            "Command:\n" +
            "Thank you for using Image Processor!\n", viewLog.toString());
  }
}
