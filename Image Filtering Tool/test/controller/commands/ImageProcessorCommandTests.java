package controller.commands;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import mocks.MockImageProcessorModel;
import model.ColorComponent;
import model.Image;
import model.ImageProcessorModel;
import model.ImageProcessorModelImpl;
import model.Pixel;
import model.RGBPixel;
import model.SimpleImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * JUnit test class for the various implementations of ImageProcessorCommand.
 */
public class ImageProcessorCommandTests {

  Appendable s;
  ImageProcessorModel mock;

  @Before
  public void setup() {
    s = new StringBuilder();
    mock = new MockImageProcessorModel(s);
    new File("test/controller/commands/saveTo/a.ppm").delete();
    new File("test/controller/commands/saveTo/a.jpg").delete();
    new File("test/controller/commands/saveTo/a.bmp").delete();
    new File("test/controller/commands/saveTo/a.png").delete();

  }

  /**
   * Tests that the runCommand method for AdjustBrightnessCommand passes correct values to the
   * model and runs the correct model method.
   */
  @Test
  public void testAdjustBrightnessRunCommand() {
    ImageProcessorCommand cmd = new AdjustBrightnessCommand(10, "a", "a1");

    try {
      cmd.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("adjustImageBrightness a a1 10\n", s.toString());
  }

  /**
   * Tests that the runCommand method for FlipHorizontalCommand passes correct values to the
   * model and runs the correct model method.
   */
  @Test
  public void testFlipHorizontalRunCommand() {
    ImageProcessorCommand cmd = new FlipHorizontalCommand("a", "a1");

    try {
      cmd.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("flipImageHorizontal a a1\n", s.toString());
  }

  /**
   * Tests that the runCommand method for FlipVerticalCommand passes correct values to the
   * model and runs the correct model method.
   */
  @Test
  public void testFlipVerticalRunCommand() {
    ImageProcessorCommand cmd = new FlipVerticalCommand("a", "a1");

    try {
      cmd.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("flipImageVertical a a1\n", s.toString());
  }


  /**
   * Tests that the runCommand method for GrayscaleCommand passes correct values to the
   * model and runs the correct model method for each possible color component.
   */
  @Test
  public void testGrayscaleRunCommand() {
    ImageProcessorCommand cmdRed = new GrayscaleCommand(ColorComponent.Red, "a", "a1");
    ImageProcessorCommand cmdGreen = new GrayscaleCommand(ColorComponent.Green, "b", "b1");
    ImageProcessorCommand cmdBlue = new GrayscaleCommand(ColorComponent.Blue, "c", "c1");
    ImageProcessorCommand cmdLuma = new GrayscaleCommand(ColorComponent.Luma, "d", "d1");
    ImageProcessorCommand cmdIntensity = new GrayscaleCommand(ColorComponent.Intensity, "e", "e1");
    ImageProcessorCommand cmdValue = new GrayscaleCommand(ColorComponent.Value, "f", "f1");

    try {
      cmdRed.runCommand(mock);
      cmdBlue.runCommand(mock);
      cmdGreen.runCommand(mock);
      cmdLuma.runCommand(mock);
      cmdIntensity.runCommand(mock);
      cmdValue.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("grayscaleImage a a1 red\n" +
            "grayscaleImage c c1 blue\n" +
            "grayscaleImage b b1 green\n" +
            "grayscaleImage d d1 luma\n" +
            "grayscaleImage e e1 intensity\n" +
            "grayscaleImage f f1 value\n", s.toString());
  }

  @Test
  public void testLoadImageRunCommand() {
    ImageProcessorCommand load1 =
            new LoadImageCommand("test/controller/commands/readFrom/im32.ppm", "im32ppm");
    ImageProcessorCommand load2 =
            new LoadImageCommand("test/controller/commands/readFrom/im22.png", "im22png");
    ImageProcessorCommand load3 =
            new LoadImageCommand("test/controller/commands/readFrom/im22.bmp", "im22bmp");
    ImageProcessorCommand load4 =
            new LoadImageCommand("test/controller/commands/readFrom/im22.jpg", "im22jpg");

    try {
      load1.runCommand(mock);
      load2.runCommand(mock);
      load3.runCommand(mock);
      load4.runCommand(mock);
    } catch (Exception e) {
      fail("Didn't find im32");
    }

    assertEquals("addImage im32ppm 2x3 image\n" +
            "addImage im22png 2x2 image\n" +
            "addImage im22bmp 2x2 image\n" +
            "addImage im22jpg 2x2 image\n", s.toString());
  }

  @Test
  public void testSaveImageRunCommand() {
    ImageProcessorCommand save1 =
            new SaveImageCommand("a", "test/controller/commands/saveTo/a.ppm");
    ImageProcessorCommand save2 =
            new SaveImageCommand("a", "test/controller/commands/saveTo/a.jpg");
    ImageProcessorCommand save3 =
            new SaveImageCommand("a", "test/controller/commands/saveTo/a.bmp");
    ImageProcessorCommand save4 =
            new SaveImageCommand("a", "test/controller/commands/saveTo/a.png");

    ImageProcessorModel m = new ImageProcessorModelImpl();
    Image a = new SimpleImage(new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(0,0,0)}});
    m.addImage("a", a);

    assertFalse(new File("test/controller/commands/saveTo/a.ppm").exists());
    assertFalse(new File("test/controller/commands/saveTo/a.jpg").exists());
    assertFalse(new File("test/controller/commands/saveTo/a.bmp").exists());
    assertFalse(new File("test/controller/commands/saveTo/a.png").exists());

    try {
      save1.runCommand(m);
      save2.runCommand(m);
      save3.runCommand(m);
      save4.runCommand(m);
    } catch (Exception e) {
      fail("Save failed.");
    }

    assertTrue(new File("test/controller/commands/saveTo/a.ppm").exists());
    assertTrue(new File("test/controller/commands/saveTo/a.jpg").exists());
    assertTrue(new File("test/controller/commands/saveTo/a.bmp").exists());
    assertTrue(new File("test/controller/commands/saveTo/a.png").exists());

    try {
      save1.runCommand(new MockImageProcessorModel(s));
      save2.runCommand(new MockImageProcessorModel(s));
      save3.runCommand(new MockImageProcessorModel(s));
      save4.runCommand(new MockImageProcessorModel(s));
    } catch (IOException e) {
      fail("Save command failed on mock model.");
    }

    assertEquals("returnImage a\n" +
            "returnImage a\n" +
            "returnImage a\n" +
            "returnImage a\n", s.toString());
  }

  @Test
  public void testBlurRunCommand() {
    ImageProcessorCommand cmd = new BlurCommand("a", "a1");

    try {
      cmd.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("filterImage a a1 " +
            "0.0625 0.125 0.0625 / 0.125 0.25 0.125 / 0.0625 0.125 0.0625 /\n", s.toString());
  }

  @Test
  public void testSharpenRunCommand() {
    ImageProcessorCommand cmd = new SharpenCommand("a", "a1");

    try {
      cmd.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("filterImage a a1 " +
            "-0.125 -0.125 -0.125 -0.125 -0.125 / " +
            "-0.125 0.25 0.25 0.25 -0.125 / " +
            "-0.125 0.25 1.0 0.25 -0.125 / " +
            "-0.125 0.25 0.25 0.25 -0.125 / " +
            "-0.125 -0.125 -0.125 -0.125 -0.125 /\n", s.toString());
  }

  @Test
  public void testSepiaRunCommand() {
    ImageProcessorCommand cmd = new SepiaCommand("a", "a1");

    try {
      cmd.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("transformImageColor a a1 " +
            "0.393 0.769 0.189 / 0.349 0.686 0.168 / 0.272 0.534 0.131 /\n", s.toString());
  }

  @Test
  public void testGrayscaleMatrixRunCommand() {
    ImageProcessorCommand cmd = new GrayscaleMatrixCommand(0.5, 0.25, 0.4, "a", "a1");

    try {
      cmd.runCommand(mock);
    } catch (IOException io) {
      fail("Unable to run command.");
    }

    assertEquals("transformImageColor a a1 " +
            "0.5 0.25 0.4 / 0.5 0.25 0.4 / 0.5 0.25 0.4 /\n", s.toString());
  }


}
