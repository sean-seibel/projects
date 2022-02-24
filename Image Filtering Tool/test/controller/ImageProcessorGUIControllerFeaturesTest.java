package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import model.ColorComponent;
import model.Image;
import view.ImageProcessorGUIView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit testing class for ImageProcessorGUIControllerFeatures.
 */
public class ImageProcessorGUIControllerFeaturesTest {

  ImageProcessorControllerFeatures cont;
  StringBuilder s;

  @Before
  public void setup() {
    s = new StringBuilder();
    cont = new ImageProcessorGUIControllerFeatures(new MockGUIView(s));
    new File("test/controller/saveTo/result.ppm").delete();
    new File("test/controller/saveTo/result.png").delete();
    new File("test/controller/saveTo/result.jpg").delete();
    new File("test/controller/saveTo/result.bmp").delete();

  }

  @Test
  public void testRunCommand() {
    cont.runCommand("brighten 50");
    cont.runCommand("brighten -40");
    cont.runCommand("sharpen");
    cont.runCommand("blur");
    cont.runCommand("grayscale");
    cont.runCommand("sepia");
    cont.runCommand("luma-component");
    //commands won't do anything to the view before you have an image loaded.
    cont.loadImage("test/controller/readFrom/im22.ppm");
    cont.runCommand("brighten 50");
    cont.runCommand("brighten -40");
    cont.runCommand("sharpen");
    cont.runCommand("blur");
    cont.runCommand("grayscale");
    cont.runCommand("sepia");
    cont.runCommand("luma-component");
    cont.loadImage("test/controller/readFrom/couch.png");
    cont.runCommand("red-component");
    cont.loadImage("test/controller/readFrom/couch.png");
    cont.runCommand("green-component");
    cont.loadImage("test/controller/readFrom/couch.png");
    cont.runCommand("blue-component");
    cont.loadImage("test/controller/readFrom/im22.ppm");
    cont.runCommand("value-component");
    cont.loadImage("test/controller/readFrom/im22.ppm");
    cont.runCommand("intensity-component");

    assertEquals("passed controller.ImageProcessorGUIControllerFeatures@436e852b\n" +
            "set image: 2x2 with pixel 0,0 = 255,0,0\n" +
            "set image: 2x2 with pixel 0,0 = 255,50,50\n" +
            "set image: 2x2 with pixel 0,0 = 215,10,10\n" +
            "set image: 2x2 with pixel 0,0 = 255,85,40\n" +
            "set image: 2x2 with pixel 0,0 = 143,69,27\n" +
            "set image: 2x2 with pixel 0,0 = 82,82,82\n" +
            "set image: 2x2 with pixel 0,0 = 111,99,77\n" +
            "set image: 2x2 with pixel 0,0 = 100,100,100\n" +
            "set image: 640x427 with pixel 0,0 = 210,199,195\n" +
            "set image: 640x427 with pixel 0,0 = 210,210,210\n" +
            "set image: 640x427 with pixel 0,0 = 210,199,195\n" +
            "set image: 640x427 with pixel 0,0 = 199,199,199\n" +
            "set image: 640x427 with pixel 0,0 = 210,199,195\n" +
            "set image: 640x427 with pixel 0,0 = 195,195,195\n" +
            "set image: 2x2 with pixel 0,0 = 255,0,0\n" +
            "set image: 2x2 with pixel 0,0 = 255,255,255\n" +
            "set image: 2x2 with pixel 0,0 = 255,0,0\n" +
            "set image: 2x2 with pixel 0,0 = 85,85,85\n", s.toString());
  }

  @Test
  public void testLoadImage() {
    cont.loadImage("test/controller/readFrom/im22.ppm");
    cont.loadImage("test/controller/readFrom/couch.png");
    cont.loadImage("test/controller/readFrom/couch-sepia.bmp");
    cont.loadImage("test/controller/readFrom/couch-gray.jpg");
    cont.loadImage("test/controller/readFrom/picture of your mom.jpg");

    assertEquals("passed controller.ImageProcessorGUIControllerFeatures@436e852b\n" +
            "set image: 2x2 with pixel 0,0 = 255,0,0\n" +
            "set image: 640x427 with pixel 0,0 = 210,199,195\n" +
            "set image: 640x427 with pixel 0,0 = 255,243,189\n" +
            "set image: 640x427 with pixel 0,0 = 201,201,201\n" +
            "show message: File reading failed.\n", s.toString());

  }

  @Test
  public void testSaveImage() {
    cont.loadImage("test/controller/readFrom/couch.png");

    assertFalse(new File("test/controller/saveTo/result.ppm").exists());
    assertFalse(new File("test/controller/saveTo/result.jpg").exists());
    assertFalse(new File("test/controller/saveTo/result.png").exists());
    assertFalse(new File("test/controller/saveTo/result.bmp").exists());

    cont.saveImage("test/controller/saveTo/result.ppm");
    cont.saveImage("test/controller/saveTo/result.jpg");
    cont.saveImage("test/controller/saveTo/result.png");
    cont.saveImage("test/controller/saveTo/result.bmp");

    assertTrue(new File("test/controller/saveTo/result.ppm").exists());
    assertTrue(new File("test/controller/saveTo/result.jpg").exists());
    assertTrue(new File("test/controller/saveTo/result.png").exists());
    assertTrue(new File("test/controller/saveTo/result.bmp").exists());

    cont.saveImage("this folder doesn't exist/result.png");

    assertEquals("passed controller.ImageProcessorGUIControllerFeatures@436e852b\n" +
            "set image: 640x427 with pixel 0,0 = 210,199,195\n" +
            "show message: Save successful.\n" +
            "show message: Save successful.\n" +
            "show message: Save successful.\n" +
            "show message: Save successful.\n" +
            "show message: File saving failed.\n", s.toString());

  }

  private static class MockGUIView implements ImageProcessorGUIView {

    StringBuilder log;

    MockGUIView(StringBuilder log) {
      this.log = log;
    }

    @Override
    public void setImage(Image im) {
      log.append("set image: ");
      log.append(im.getWidth());
      log.append("x");
      log.append(im.getHeight());
      log.append(" with pixel 0,0 = ");
      log.append(im.getPixelAt(0,0).getComponent(ColorComponent.Red));
      log.append(",");
      log.append(im.getPixelAt(0,0).getComponent(ColorComponent.Green));
      log.append(",");
      log.append(im.getPixelAt(0,0).getComponent(ColorComponent.Blue));
      log.append("\n");
    }

    @Override
    public void passFeatures(ImageProcessorControllerFeatures cont) {
      log.append("passed ");
      log.append(cont.toString());
      log.append("\n");
    }

    @Override
    public void showMessage(String message) {
      log.append("show message: ");
      log.append(message);
      log.append("\n");
    }
  }
}
