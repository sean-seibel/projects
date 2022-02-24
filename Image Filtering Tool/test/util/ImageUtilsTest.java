package util;

import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import model.ColorComponent;
import model.Image;
import model.Pixel;
import model.RGBPixel;
import model.SimpleImage;
import utils.ImageUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the ImageUtils class.
 */
public class ImageUtilsTest {

  Image im22;
  Image im32;

  @Before
  public void setup() {
    im22 = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}});
    im32 = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)}});
    new File("test/util/saveTo/im22.ppm").delete();
    new File("test/util/saveTo/im32.ppm").delete();
    new File("test/util/saveTo/im22.jpg").delete();
    new File("test/util/saveTo/im32.jpg").delete();
    new File("test/util/saveTo/im22.bmp").delete();
    new File("test/util/saveTo/im32.bmp").delete();
    new File("test/util/saveTo/im22.png").delete();
    new File("test/util/saveTo/im32.png").delete();

  }

  @Test
  public void testReadPPM() {
    try {
      assertTrue(sameColorPixelsInImage(im22,
              ImageUtils.readPPM("test/util/readFrom/im22.ppm")));
      assertTrue(sameColorPixelsInImage(im32,
              ImageUtils.readPPM("test/util/readFrom/im32.ppm")));
    } catch (FileNotFoundException fnfE) {
      fail("Did not find im22 or im32");
    }

    try {
      ImageUtils.readPPM("test/util/readFrom/im44.ppm");
      fail("Mistakenly found im44");
    } catch (FileNotFoundException fnfE) {
      assertFalse(new File("test/util/readFrom/im44.ppm").exists());
    }
  }

  @Test
  public void testReadErrorNotRaw() {
    try {
      ImageUtils.readPPM("test/util/readFrom/notRaw.ppm");
    } catch (FileNotFoundException fnfE) {
      assertEquals("File found was not raw ppm", fnfE.getMessage());
    }

    try {
      assertTrue(sameColorPixelsInImage(
              ImageUtils.readPPM("test/util/readFrom/diffMax.ppm"),
              new SimpleImage(new Pixel[][]{
                      {new RGBPixel(0,255,128), new RGBPixel(0,0,255)},
                      {new RGBPixel(191, 64, 191), new RGBPixel(128,128,128)}})));
    } catch (FileNotFoundException fnfE) {
      fail("Didn't find diffMax.ppm");
    }
  }

  @Test
  public void testSavePPM() {
    File file22 = new File("test/util/saveTo/im22.ppm");
    File file32 = new File("test/util/saveTo/im32.ppm");

    assertFalse(file22.exists());
    assertFalse(file32.exists());

    try {
      ImageUtils.savePPM(im22, "test/util/saveTo/im22.ppm");

      assertTrue(file22.exists());
      assertFalse(file32.exists());

      ImageUtils.savePPM(im32, "test/util/saveTo/im32.ppm");

      assertTrue(file22.exists());
      assertTrue(file32.exists());
    } catch (IOException ioE) {
      fail("IO failed.");
    }

    try {
      assertTrue(sameColorPixelsInImage(im22,
              ImageUtils.readPPM("test/util/saveTo/im22.ppm")));
      assertTrue(sameColorPixelsInImage(im32,
              ImageUtils.readPPM("test/util/saveTo/im32.ppm")));
    } catch (FileNotFoundException fnfE) {
      fail("im22 or im32 not saved to correct path.");
    }
  }

  @Test
  public void testReadOther() {

    try {
      Image im22 = ImageUtils.readOther("test/util/readFrom/im22.jpg");
      Image im32 = ImageUtils.readOther("test/util/readFrom/im32.jpg");
      assertEquals(2, im22.getHeight());
      assertEquals(2, im22.getWidth());
      assertEquals(3, im32.getHeight());
      assertEquals(2, im32.getWidth());
    } catch (IOException ioE) {
      fail("Did not find im22 or im32");
    }

    try {
      assertTrue(sameColorPixelsInImage(im22,
              ImageUtils.readOther("test/util/readFrom/im22.bmp")));
      assertTrue(sameColorPixelsInImage(im32,
              ImageUtils.readOther("test/util/readFrom/im32.bmp")));
    } catch (IOException ioE) {
      fail("Did not find im22 or im32");
    }

    try {
      assertTrue(sameColorPixelsInImage(im22,
              ImageUtils.readOther("test/util/readFrom/im22.png")));
      assertTrue(sameColorPixelsInImage(im32,
              ImageUtils.readOther("test/util/readFrom/im32.png")));
    } catch (IOException ioE) {
      fail("Did not find im22 or im32");
    }

    try {
      ImageUtils.readOther("test/util/readFrom/im44.jpg");
      fail("Mistakenly found im44");
    } catch (IOException ioE) {
      assertFalse(new File("test/util/readFrom/im44.jpg").exists());
    }

    try {
      ImageUtils.readOther("test/util/readFrom/im44.bmp");
      fail("Mistakenly found im44");
    } catch (IOException ioE) {
      assertFalse(new File("test/util/readFrom/im44.bmp").exists());
    }

    try {
      ImageUtils.readOther("test/util/readFrom/im44.png");
      fail("Mistakenly found im44");
    } catch (IOException ioE) {
      assertFalse(new File("test/util/readFrom/im44.png").exists());
    }
  }

  @Test
  public void testSaveOther() {
    File jpg22 = new File("test/util/saveTo/im22.jpg");
    File jpg32 = new File("test/util/saveTo/im32.jpg");

    File bmp22 = new File("test/util/saveTo/im22.bmp");
    File bmp32 = new File("test/util/saveTo/im32.bmp");

    File png22 = new File("test/util/saveTo/im22.png");
    File png32 = new File("test/util/saveTo/im32.png");

    assertFalse(jpg22.exists());
    assertFalse(jpg32.exists());

    assertFalse(bmp22.exists());
    assertFalse(bmp32.exists());

    assertFalse(png22.exists());
    assertFalse(png32.exists());

    try {
      ImageUtils.saveOther(im22, "test/util/saveTo/im22.jpg");
      ImageUtils.saveOther(im32, "test/util/saveTo/im32.jpg");

      assertTrue(jpg22.exists());
      assertTrue(jpg32.exists());

      assertFalse(bmp22.exists());
      assertFalse(bmp32.exists());

      assertFalse(png22.exists());
      assertFalse(png32.exists());

      ImageUtils.saveOther(im22, "test/util/saveTo/im22.bmp");
      ImageUtils.saveOther(im32, "test/util/saveTo/im32.bmp");

      assertTrue(jpg22.exists());
      assertTrue(jpg32.exists());

      assertTrue(bmp22.exists());
      assertTrue(bmp32.exists());

      assertFalse(png22.exists());
      assertFalse(png32.exists());

      ImageUtils.saveOther(im22, "test/util/saveTo/im22.png");
      ImageUtils.saveOther(im32, "test/util/saveTo/im32.png");

      assertTrue(jpg22.exists());
      assertTrue(jpg32.exists());

      assertTrue(bmp22.exists());
      assertTrue(bmp32.exists());

      assertTrue(png22.exists());
      assertTrue(png32.exists());

    } catch (IOException ioE) {
      fail("IO failed.");
    }

    try {
      assertEquals(2 ,
              ImageUtils.readOther("test/util/saveTo/im22.jpg").getHeight());
      assertEquals(2 ,
              ImageUtils.readOther("test/util/saveTo/im22.jpg").getWidth());
      assertEquals(3 ,
              ImageUtils.readOther("test/util/saveTo/im32.jpg").getHeight());
      assertEquals(2 ,
              ImageUtils.readOther("test/util/saveTo/im32.jpg").getWidth());

      assertTrue(sameColorPixelsInImage(im22,
              ImageUtils.readOther("test/util/saveTo/im22.bmp")));
      assertTrue(sameColorPixelsInImage(im32,
              ImageUtils.readOther("test/util/saveTo/im32.bmp")));

      assertTrue(sameColorPixelsInImage(im22,
              ImageUtils.readOther("test/util/saveTo/im22.png")));
      assertTrue(sameColorPixelsInImage(im32,
              ImageUtils.readOther("test/util/saveTo/im32.png")));

    } catch (IOException ioE) {
      fail("im22 or im32 not saved to correct path.");
    }
  }

  @Test
  public void testHistogram() {

    Image couch;
    BufferedImage couchHisto;

    try {
      couch = ImageUtils.readOther("test/util/readFrom/couch.png");
      couchHisto = ImageIO.read(new File("test/util/readFrom/couchHisto.png"));
      assertTrue(sameColorPixelsBufferedImage(
              ImageUtils.makeHistogram(couch, 256, 10),
              couchHisto));
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testMakeImageIcon() {
    Image couch;
    ImageIcon couchIcon;

    try {
      couch = ImageUtils.readOther("test/util/readFrom/couch.png");
      couchIcon = ImageUtils.makeImageIcon(couch);
      assertEquals(couch.getWidth(), couchIcon.getIconWidth());
      assertEquals(640, couchIcon.getIconWidth());
      assertEquals(couch.getHeight(), couchIcon.getIconHeight());
      assertEquals(427, couchIcon.getIconHeight());
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }


  private boolean sameColorPixelsInImage(Image im1, Image im2) {
    if (im1.getHeight() != im2.getHeight() || im1.getWidth() != im2.getWidth()) {
      return false;
    }

    boolean sameSoFar = true;

    for (int r = 0; r < im1.getHeight() && sameSoFar; r++) {
      for (int c = 0; c < im1.getWidth() && sameSoFar; c++) {
        Pixel p1 = im1.getPixelAt(r,c);
        Pixel p2 = im2.getPixelAt(r,c);
        sameSoFar &= p1.getComponent(ColorComponent.Red) == p2.getComponent(ColorComponent.Red) &&
                p1.getComponent(ColorComponent.Green) == p2.getComponent(ColorComponent.Green) &&
                p1.getComponent(ColorComponent.Blue) == p2.getComponent(ColorComponent.Blue);
      }
    }

    return sameSoFar;
  }


  private boolean sameColorPixelsBufferedImage(BufferedImage img1, BufferedImage img2) {
    if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
      return false;
    }
    for (int x = 0; x < img1.getWidth(); x++) {
      for (int y = 0; y < img1.getHeight(); y++) {
        if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
          return false;
        }
      }
    }
    return true;
  }
}
