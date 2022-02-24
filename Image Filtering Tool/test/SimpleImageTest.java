import org.junit.Before;
import org.junit.Test;

import model.ColorComponent;
import model.Image;
import model.Pixel;
import model.RGBPixel;
import model.SimpleImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests the SimpleImage class.
 */
public class SimpleImageTest {

  Image image22;
  Image image32;

  /**
   * Sets up test images.
   */
  @Before
  public void setup() {
    image22 = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}});
    image32 = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)}});
  }

  /**
   * Tests SimpleImage constructor.
   */
  @Test
  public void testSimpleImageConstructor() {
    Image test1 = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}});
    assertTrue(sameColorPixelsInImage(test1, image22));
    assertFalse(sameColorPixelsInImage(test1, image32));
    assertFalse(sameColorPixelsInImage(test1, new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(254,0,0)}, //just a bit different
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}})));

    Image test2 = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)}});
    assertEquals(3, test2.getHeight());
    assertEquals(2,test2.getWidth());
  }

  /**
   * Tests that SimpleImage constructor throws exception if given pixels are null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullPixelsException() {
    Image test = new SimpleImage(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkNonRectangularArrayError1() {
    new SimpleImage(new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(0,0,0)},
        {new RGBPixel(120,120,120), new RGBPixel(44,44,34), new RGBPixel(0,0,255)}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkNonRectangularArrayError2() {
    new SimpleImage(new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(0,0,0)},
        {new RGBPixel(120,120,120), new RGBPixel(44,44,34)},
        null});
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkNullPixelError() {
    new SimpleImage(new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(0,0,0)},
        {new RGBPixel(120,120,120), null}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkArrayDimension0Error1() {
    new SimpleImage(new Pixel[][]{});
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkArrayDimension0Error2() {
    new SimpleImage(new Pixel[][]{{},{},{},{},{}});
  }

  /**
   * Tests the getHeight method in SimpleImage.
   */
  @Test
  public void testGetHeight() {
    assertEquals(2, image22.getHeight());
    assertEquals(3, image32.getHeight());
  }

  /**
   * Tests the getWidth method in SimpleImage.
   */
  @Test
  public void testGetWidth() {
    assertEquals(2, image22.getWidth());
    assertEquals(2, image32.getWidth());
  }

  /**
   * Tests the getPixelAt method in SimpleImage.
   */
  @Test
  public void testGetPixelAt() {
    assertEquals(255, image22.getPixelAt(0,0).getComponent(ColorComponent.Red));
    assertEquals(0, image22.getPixelAt(1,0).getComponent(ColorComponent.Green));
    assertEquals(90, image22.getPixelAt(1,1).getComponent(ColorComponent.Intensity));

    assertEquals(124, image32.getPixelAt(2,0).getComponent(ColorComponent.Blue));
    assertEquals(90, image32.getPixelAt(0,0).getComponent(ColorComponent.Luma));
    assertEquals(255, image32.getPixelAt(0,1).getComponent(ColorComponent.Value));
  }

  /**
   * tests that getPixelAt throws exception if given row is negative.
   */
  @Test(expected = IllegalArgumentException.class)
  public void getPixelNegRow() {
    image32.getPixelAt(-1,0);
  }

  /**
   * tests that getPixelAt throws exception if given col is negative.
   */
  @Test(expected = IllegalArgumentException.class)
  public void getPixelNegCol() {
    image32.getPixelAt(0,-1);
  }

  /**
   * tests that getPixelAt throws exception if given row is greater than
   * SimpleImage height.
   */
  @Test(expected = IllegalArgumentException.class)
  public void getPixelRowTooHigh() {
    image32.getPixelAt(3,1);
  }

  /**
   * tests that getPixelAt throws exception if given col is greater than
   * SimpleImage width.
   */
  @Test(expected = IllegalArgumentException.class)
  public void getPixelColTooHigh() {
    image32.getPixelAt(0,2);
  }

  /**
   * tests that flipVertical method in SimpleImage returns new Image with Pixel array flipped
   * vertically.
   */
  @Test
  public void testFlipVertical() {
    Image image22FlipVer = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(90,90,90), new RGBPixel(180,180,0)}});

    assertTrue(sameColorPixelsInImage(image22.flipVertical(), image22FlipVer));

    Image image32FlipVer = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(255,222,124),
                    new RGBPixel(0,0,255),
                    new RGBPixel(0,100,255)},{
                    new RGBPixel(200,175,115),
                    new RGBPixel(0,0,255),
                    new RGBPixel(0,100,255)}});

    assertTrue(sameColorPixelsInImage(image32.flipVertical(), image32FlipVer));
  }

  /**
   * tests that flipHorizontal method in SimpleImage returns new Image with Pixel array flipped
   * horizontally.
   */
  @Test
  public void testFlipHorizontal() {
    Image image22FlipHor = new SimpleImage(
            new Pixel[][]{{new RGBPixel(180,180,0), new RGBPixel(90,90,90)},
                {new RGBPixel(255,0,0), new RGBPixel(255,0,0)},});

    assertTrue(sameColorPixelsInImage(image22.flipHorizontal(), image22FlipHor));

    Image image32FlipHor = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},});

    assertTrue(sameColorPixelsInImage(image32.flipHorizontal(), image32FlipHor));
  }

  /**
   * tests that adjustBrightness method in SimpleImage returns new Image with each pixel
   * brightness adjusted gy the given delta.
   */
  @Test
  public void testAdjustBrightness() {
    Image image22darker = new SimpleImage(
            new Pixel[][]{{new RGBPixel(205,0,0), new RGBPixel(205,0,0)},
                {new RGBPixel(130,130,0), new RGBPixel(40,40,40)}});

    assertTrue(sameColorPixelsInImage(image22.adjustBrightness(-50), image22darker));

    Image image32brighter = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(40,140,255),
                    new RGBPixel(40,40,255),
                    new RGBPixel(255,255,164)},{
                    new RGBPixel(40,140,255),
                    new RGBPixel(40,40,255),
                    new RGBPixel(240,215,155)}});

    assertTrue(sameColorPixelsInImage(image32.adjustBrightness(40), image32brighter));
  }

  /**
   * Tests that grayscale method in SimpleImage returns new Image grayscaled by given
   * component.
   */
  @Test
  public void testGrayscaleImage()  {
    Image image22GrayRed = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(255,255,255)},
                {new RGBPixel(180,180,180), new RGBPixel(90,90,90)}});

    Image image22GrayValue = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(255,255,255)},
                {new RGBPixel(180,180,180), new RGBPixel(90,90,90)}});

    Image image22GrayLuma = new SimpleImage(
            new Pixel[][]{{new RGBPixel(54,54,54), new RGBPixel(54,54,54)},
                {new RGBPixel(167,167,167), new RGBPixel(90,90,90)}});

    Image image32GrayGreen = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(100,100,100),
                    new RGBPixel(0,0,0),
                    new RGBPixel(222,222,222)},{
                    new RGBPixel(100,100,100),
                    new RGBPixel(0,0,0),
                    new RGBPixel(175,175,175)}});

    Image image32GrayBlue = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(255,255,255),
                    new RGBPixel(255,255,255),
                    new RGBPixel(124,124,124)},{
                    new RGBPixel(255,255,255),
                    new RGBPixel(255,255,255),
                    new RGBPixel(115,115,115)}});

    Image image32GrayIntensity = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(118,118,118),
                    new RGBPixel(85,85,85),
                    new RGBPixel(200,200,200)},{
                    new RGBPixel(118,118,118),
                    new RGBPixel(85,85,85),
                    new RGBPixel(163,163,163)}});

    assertTrue(sameColorPixelsInImage(image22.grayscale(ColorComponent.Red), image22GrayRed));
    assertTrue(sameColorPixelsInImage(image22.grayscale(ColorComponent.Value), image22GrayValue));
    assertTrue(sameColorPixelsInImage(image22.grayscale(ColorComponent.Luma), image22GrayLuma));
    assertTrue(sameColorPixelsInImage(image32.grayscale(ColorComponent.Green), image32GrayGreen));
    assertTrue(sameColorPixelsInImage(image32.grayscale(ColorComponent.Blue), image32GrayBlue));
    assertTrue(sameColorPixelsInImage(
            image32.grayscale(ColorComponent.Intensity), image32GrayIntensity));
  }

  @Test
  public void testTransformColor() {
    double[][] matrix1 = new double[][]{
            {0.3, 0.3, 0.2},
            {0.5, 1.0, -0.5},
            {0.7, 0.0, 0.0}};

    double[][] matrix2 = new double[][]{
            {0.1, 0.8, 0.1},
            {0.33, 0.33, 0.34},
            {1.0, -1.0, 1.0}};

    assertTrue(sameColorPixelsInImage(image22.transformColor(matrix1), new SimpleImage(
            new Pixel[][]{{new RGBPixel(77,128,179), new RGBPixel(77,128,179)},
                {new RGBPixel(108,255,126), new RGBPixel(72,90,63)}})));

    assertTrue(sameColorPixelsInImage(image32.transformColor(matrix2), new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(106,120,155),
                    new RGBPixel(26,87,255),
                    new RGBPixel(216,200,157)},{
                    new RGBPixel(106,120,155),
                    new RGBPixel(26,87,255),
                    new RGBPixel(172,163,140)}})));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException1() {
    image22.transformColor(new double[][]{{0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException2() {
    image22.transformColor(new double[][]{{0.33, 0.33}, {0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException3() {
    image22.transformColor(new double[][]{{0.33, 0.33, 0.33}, {0.33, 0.33}, {0.33, 0.33, 0.33}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException4() {
    image22.transformColor(new double[][]{{0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}, {0.33, 0.33}});
  }

  @Test
  public void testFilter() {
    Image im22f = image22.filter(new double[][]{{-0.5, 2.0, -0.5}});
    Image im32f = image32.filter(new double[][]{{0.125, 0.125, 0.125},
        {0.125, 0, 0.125},
        {0.125, 0.125, 0.125}});

    image22 = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}});
    image32 = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)}});

    assertTrue(sameColorPixelsInImage(im22f, new SimpleImage(
            new Pixel[][]{{new RGBPixel(0,0,0), new RGBPixel(233,255,0)},
                {new RGBPixel(0,0,0), new RGBPixel(0,0,0)}})));

    assertTrue(sameColorPixelsInImage(im32f, new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,13,96),
                    new RGBPixel(57,75,126),
                    new RGBPixel(25,22,78)},{
                    new RGBPixel(0,13,96),
                    new RGBPixel(57,75,126),
                    new RGBPixel(32,28,79)}})));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterException1() {
    image22.filter(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterException2() {
    image22.filter(new double[][]{});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterException3() {
    image22.filter(new double[][]{{},{},{}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterException4() {
    image22.filter(new double[][]{{1.0},{2.0},null});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterException5() {
    image22.filter(new double[][]{{1.0},{2.0}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterException6() {
    image22.filter(new double[][]{{1.0,0.5,0.6},{2.0},{-1.0}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterException7() {
    image22.filter(new double[][]{{0.5,0.5,0.5},{-0.5,0.5,-0.5}});
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
}
